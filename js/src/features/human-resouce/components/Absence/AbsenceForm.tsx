import React from "react";
import { Form, Button, Input, Select } from "antd";
import DatePicker from "components/antd/DatePicker";
import { AbsenceForm as AbsenceFormModel, Absence } from "HumanResourceModels";
import { FormErrorMap } from "MyTypes";
import { serializeForm, deserializeForm } from "./absence.helper";

const { RangePicker } = DatePicker;

interface Props {
  mode?: "CREATE" | "UPDATE";
  initialValues?: Absence | null;
  onSubmit: (form: AbsenceFormModel) => any;
  errors: FormErrorMap;
}

export const AbsenceForm: React.FC<Props> = (props) => {
  const { onSubmit, mode = "CREATE" } = props;
  const [form] = Form.useForm();

  const initialValues = props.initialValues
    ? deserializeForm(props.initialValues)
    : {};
  const handleFinish = (values: any) => onSubmit(serializeForm(values));

  return (
    <Form form={form} onFinish={handleFinish} initialValues={initialValues}>
      <Form.Item name="range">
        <RangePicker />
      </Form.Item>

      <Form.Item name="reason">
        <Select placeholder="Enter Reason">
          <Select.Option value="VACATION">Vacation</Select.Option>
          <Select.Option value="DAYON">Day on</Select.Option>
          <Select.Option value="SICK">Sick</Select.Option>
          <Select.Option value="PERSONAL">Personal</Select.Option>
        </Select>
      </Form.Item>

      <Form.Item name="comment">
        <Input.TextArea placeholder="Enter Comment" />
      </Form.Item>

      <Form.Item>
        <Button htmlType="submit" loading={false} type="primary">
          {mode === "CREATE" ? "Create" : "Update"} Absence
        </Button>
      </Form.Item>
    </Form>
  );
};
