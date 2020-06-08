import React from "react";
import { Link } from "react-router-dom";
import { Avatar } from "antd";

import { EmployeeShort } from "HumanResourceModels";
import { getEmployeeInitials } from "./helpers";

interface Props {
  employee: EmployeeShort;
}

export const HumanResourceCalendarUser: React.FC<Props> = ({ employee }) => {
  const name =
    employee.firstName && employee.lastName
      ? `${employee.firstName} ${employee.lastName}`
      : employee.userId;

  return (
    <div>
      <Link to={`/users/${employee.userId}`}>
        <Avatar>
          {getEmployeeInitials(employee.firstName)}
          {getEmployeeInitials(employee.lastName)}
        </Avatar>
        <span className="title">{name}</span>
      </Link>
    </div>
  );
};
