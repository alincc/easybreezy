import React, { useCallback, useMemo, useState } from "react";
import Calendar from "react-calendar";
import { Card, Col, Row } from "antd";
import isSameDay from "date-fns/fp/isSameDay";
import format from "date-fns/fp/format";

import { HolidayCalendarEditDateForm } from "./HolidayCalendarEditDateForm";
import { HolidayCalendarAddDateForm } from "./HolidayCalendarAddDateForm";
import { Holiday } from "LocationModels";

import "./HolidayCalendar.scss";
import "react-calendar/dist/Calendar.css";

type Props = {
  holidays: Holiday[];
};

export const HolidayCalendar: React.FC<Props> = ({ holidays }) => {
  const [activeDate, setActiveDate] = useState(new Date());

  const activeDateHolidayName = useMemo(() => {
    const holiday = holidays.find((holiday) =>
      isSameDay(activeDate, new Date(holiday.day)),
    );

    return holiday ? holiday.name : "";
  }, [activeDate, holidays]);

  const getHoliday = useCallback(
    (date: Date) => {
      return holidays.find((holiday) => isSameDay(date, new Date(holiday.day)));
    },
    [holidays],
  );

  const isWorkingDay = useCallback(
    (date: Date) => {
      const holiday = getHoliday(date);

      if (holiday) {
        return holiday.isWorkingDay;
      }

      return false;
    },
    [getHoliday],
  );

  return (
    <Row gutter={16}>
      <Col>
        <Card>
          <Calendar
            value={activeDate}
            onClickDay={(date) => setActiveDate(date)}
            tileClassName={({ date }) => {
              const holiday = getHoliday(date);

              if (holiday) {
                return holiday.isWorkingDay ? " work" : "holiday";
              }

              return "";
            }}
          />
        </Card>
      </Col>
      <Col>
        <Card style={{ width: 400 }} title={format("yyyy-MM-dd", activeDate)}>
          {!activeDateHolidayName ? (
            <HolidayCalendarAddDateForm activeDate={activeDate} />
          ) : (
            <HolidayCalendarEditDateForm
              activeDate={activeDate}
              activeDateHolidayName={activeDateHolidayName}
              isWorkingDay={isWorkingDay(activeDate)}
            />
          )}
        </Card>
      </Col>
    </Row>
  );
};
