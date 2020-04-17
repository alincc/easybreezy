declare module "HumanResourceModels" {
  /** START types for stubs */
  type UserVacation = {
    from: string;
    to: string;
    description: string;
  };

  export type UserVacations = {
    id: number;
    username: string;
    avatar: string;
    vacations: UserVacation[];
  };

  export type CalendarVacationItem = {
    id: string;
    group: number;
    title: string;
    start_time: number;
    end_time: number;
  };

  export type CalendarVacationGroup = {
    id: string;
    title: string;
    item: EmployeeShort;
  };

  /** END types for stubs */

  export type EmployeeShort = {
    userId: string;
    firstName: string | null;
    lastName: string | null;
  };

  export type EmployeeNote = {
    id: string;
    text: string;
    archived: boolean;
    authorId: string;
    createdAt: string; // "2020-04-10T16:51:07.575132";
  };

  export type EmployeeSalary = {
    id: string;
    amount: number;
    comment: string;
    since: string;
    till: string;
  };

  export type EmployeePosition = {
    id: string;
    title: string;
    since: string;
    till: string | null;
  };

  export type Employee = {
    userId: string;
    firstName: string | null;
    lastName: string | null;
    birthday: string | null; // "2000-01-01";
    bio: string | null;
    skills: string[];
    notes?: EmployeeNote[];
    salaries?: EmployeeSalary[];
    positions?: EmployeePosition[];
    contacts?: EmployeeContact[];
  };

  export type EmployeeResponse = {
    data: Employee;
  };

  export type UpdateBirthdayRequestParams = {
    userId: string;
    birthday: string;
  };

  export type EmployeeContact = {
    type: string;
    value: string;
  };

  export type ContactsForm = {
    contacts: EmployeeContact[];
  };

  export type SpecifySkillsRequestParams = {
    userId: string;
    skills: string[];
  };

  export type AddNoteRequestParams = {
    userId: string;
    text: string;
  };

  export type ApplyPositionRequestParams = {
    userId: string;
    position: string;
  };

  export type ApplySalaryRequestParams = {
    userId: string;
    amount: string;
    comment: string;
  };

  export type ApplySalaryForm = {
    amount: string;
    comment: string;
  };
}
