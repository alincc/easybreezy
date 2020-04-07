import React, { useCallback } from "react";
import { Form, Input, Button, Select, DatePicker } from "antd";
import { Location, AssignLocationForm } from "LocationModels";
import { FormErrorMap } from "MyTypes";
import { useFormServerErrors } from "hooks/useFormServerErrors";
const { Option } = Select;

interface Props {
  userId: string;
  locations: Location[];
  errors: FormErrorMap;
  assign: (form: AssignLocationForm) => void;
}

const DATE_FORMAT = "YYYY-MM-DD";
const initialFormValues = { extraVacationDays: 0 };

export const LocationAssignForm: React.FC<Props> = ({
  userId,
  locations,
  errors,
  assign,
}) => {
  const [form] = Form.useForm();

  useFormServerErrors(form, errors, [
    "startedAt",
    "endedAt",
    "extraVacationDays",
    "locationId",
  ]);

  const onFinish = useCallback(
    (values: any) => {
      const startedAt = values.startedAt.format(DATE_FORMAT);
      const endedAt = values.endedAt.format(DATE_FORMAT);

      assign({
        startedAt,
        endedAt,
        userId,
        locationId: values.locationId,
        extraVacationDays: values.extraVacationDays,
      });
    },
    [userId, assign],
  );

  const onFinishFailed = useCallback((errorInfo: any) => {
    console.log("onFinishFailed:", errorInfo);
  }, []);

  return (
    <Form
      form={form}
      labelCol={{ span: 8 }}
      onFinish={onFinish}
      onFinishFailed={onFinishFailed}
      initialValues={initialFormValues}
    >
      <Form.Item
        label="Started at"
        name="startedAt"
        rules={[{ required: true, message: "Please input Started at!" }]}
      >
        <DatePicker />
      </Form.Item>

      <Form.Item
        label="Ended at"
        name="endedAt"
        rules={[{ required: true, message: "Please input Ended at!" }]}
      >
        <DatePicker />
      </Form.Item>

      <Form.Item
        label="Extra vacation days"
        name="extraVacationDays"
        rules={[
          { required: true, message: "Please input Extra vacation days!" },
        ]}
      >
        <Input />
      </Form.Item>

      <Form.Item
        label="Location"
        name="locationId"
        rules={[{ required: true, message: "Please input Location!" }]}
      >
        <Select
          showSearch
          placeholder="Select a location"
          optionFilterProp="children"
          filterOption={(input, option) =>
            option!.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
          }
        >
          {locations.map((item) => (
            <Option key={item.id} value={item.id}>
              {item.name}
            </Option>
          ))}
        </Select>
      </Form.Item>

      <Form.Item>
        <Button type="primary" htmlType="submit">
          Assign
        </Button>
      </Form.Item>
    </Form>
  );
};
