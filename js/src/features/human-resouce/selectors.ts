import { RootState } from "MyTypes";

export const employees = (state: RootState) => state.humanResource.employees;

export const absences = (state: RootState) => state.humanResource.absences;

export const employeeDetails = (state: RootState) =>
  state.humanResource.details;

export const employeeLocations = (state: RootState) =>
  state.humanResource.location;

export const isAssignLocationFormVisible = (state: RootState) =>
  state.humanResource.location.showAssignForm;

export const isEditEmployeeLocationFormVisible = (state: RootState) =>
  state.humanResource.location.employeeLocationToEdit !== null;
