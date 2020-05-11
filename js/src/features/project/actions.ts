import { createAction, createAsyncAction } from "typesafe-actions";

import { FormError, Paging } from "MyTypes";
import {
  ProjectList,
  EditProjectStatusRequest,
  CreateProjectRequest,
  EditProjectDescriptionRequest,
  EditProjectRoleRequest,
  RemoveProjectRoleRequest,
  CreateProjectRoleRequest,
  Project,
  ProjectsRequest,
  RolePermissions,
  EditProjectSlugRequest,
  CreateProjectTeamRequest,
  ProjectTeam,
  EditProjectTeamMemberRoleRequest,
  RemoveProjectTeamMemberRequest,
  AddProjectTeamMemberRequest,
  ChangeProjectTeamStatusRequest,
} from "ProjectModels";
import { EmployeeShort } from "HumanResourceModels";

//PROJECT
export const fetchProjectAsync = createAsyncAction(
  "FETCH_PROJECT_REQUEST",
  "FETCH_PROJECT_SUCCESS",
  "FETCH_PROJECT_FAILURE",
)<string, Project, any>();

// PROJECTS
export const fetchProjectsAsync = createAsyncAction(
  "FETCH_PROJECTS_REQUEST",
  "FETCH_PROJECTS_SUCCESS",
  "FETCH_PROJECTS_FAILURE",
)<ProjectsRequest, Paging<ProjectList>, string>();

//CREATE PROJECT
export const openProjectCreateForm = createAction("OPEN_PROJECT_CREATE_FORM")();

export const closeProjectCreateForm = createAction(
  "CLOSE_PROJECT_CREATE_FORM",
)();

export const createProjectAsync = createAsyncAction(
  "CREATE_PROJECT_REQUEST",
  "CREATE_PROJECT_SUCCESS",
  "CREATE_PROJECT_FAILURE",
)<CreateProjectRequest, undefined, FormError[]>();

// ROLE
export const createProjectRoleAsync = createAsyncAction(
  "CREATE_PROJECT_ROLE_REQUEST",
  "CREATE_PROJECT_ROLE_SUCCESS",
  "CREATE_PROJECT_ROLE_FAILURE",
)<CreateProjectRoleRequest, undefined, FormError[]>();

export const editProjectRoleAsync = createAsyncAction(
  "EDIT_PROJECT_ROLE_REQUEST",
  "EDIT_PROJECT_ROLE_SUCCESS",
  "EDIT_PROJECT_ROLE_FAILURE",
)<EditProjectRoleRequest, undefined, FormError[]>();

export const removeProjectRoleAsync = createAsyncAction(
  "REMOVE_PROJECT_ROLE_REQUEST",
  "REMOVE_PROJECT_ROLE_SUCCESS",
  "REMOVE_PROJECT_ROLE_FAILURE",
)<RemoveProjectRoleRequest, undefined, FormError[]>();

export const fetchProjectRoleAsync = createAsyncAction(
  "FETCH_PROJECT_ROLE_PERMISSIONS_REQUEST",
  "FETCH_PROJECT_ROLE_PERMISSIONS_SUCCESS",
  "FETCH_PROJECT_ROLE_PERMISSIONS_FAILURE",
)<undefined, RolePermissions, FormError[]>();

// STATUS
export const changeProjectStatusAsync = createAsyncAction(
  "CHANGE_PROJECT_STATUS_REQUEST",
  "CHANGE_PROJECT_STATUS_SUCCESS",
  "CHANGE_PROJECT_STATUS_FAILURE",
)<EditProjectStatusRequest, undefined, FormError[]>();

// DESCRIPTION
export const editProjectDescriptionAsync = createAsyncAction(
  "EDIT_PROJECT_DESCRIPTION_REQUEST",
  "EDIT_PROJECT_DESCRIPTION_SUCCESS",
  "EDIT_PROJECT_DESCRIPTION_FAILURE",
)<EditProjectDescriptionRequest, undefined, FormError[]>();

// SLUG
export const editProjectSlugAsync = createAsyncAction(
  "EDIT_PROJECT_SLUG_REQUEST",
  "EDIT_PROJECT_SLUG_SUCCESS",
  "EDIT_PROJECT_SLUG_FAILURE",
)<EditProjectSlugRequest, undefined, FormError[]>();

//TEAM
export const createProjectTeamAsync = createAsyncAction(
  "CREATE_PROJECT_PROJECT_REQUEST",
  "CREATE_PROJECT_PROJECT_SUCCESS",
  "CREATE_PROJECT_PROJECT_FAILURE",
)<CreateProjectTeamRequest, undefined, FormError[]>();

export const openProjectTeamCreateFormAction = createAction(
  "OPEN_PROJECT_TEAM_CREATE_FORM",
)();

export const closeProjectTeamCreateFormAction = createAction(
  "CLOSE_PROJECT_TEAM_CREATE_FORM",
)();

export const fetchProjectTeamAsync = createAsyncAction(
  "FETCH_PROJECT_TEAM_REQUEST",
  "FETCH_PROJECT_TEAM_SUCCESS",
  "FETCH_PROJECT_TEAM_FAILED",
)<string, ProjectTeam, FormError[]>();

export const editProjectTeamMemberRoleAsync = createAsyncAction(
  "EDIT_PROJECT_TEAM_MEMBER_ROLE_REQUEST",
  "EDIT_PROJECT_TEAM_MEMBER_ROLE_SUCCESS",
  "EDIT_PROJECT_TEAM_MEMBER_ROLE_FAILED",
)<EditProjectTeamMemberRoleRequest, undefined, FormError[]>();

export const removeProjectTeamMemberAsync = createAsyncAction(
  "REMOVE_PROJECT_TEAM_MEMBER_REQUEST",
  "REMOVE_PROJECT_TEAM_MEMBER_SUCCESS",
  "REMOVE_PROJECT_TEAM_MEMBER_FAILED",
)<RemoveProjectTeamMemberRequest, undefined, FormError[]>();

export const addProjectTeamMemberAsync = createAsyncAction(
  "ADD_PROJECT_TEAM_MEMBER_REQUEST",
  "ADD_PROJECT_TEAM_MEMBER_SUCCESS",
  "ADD_PROJECT_TEAM_MEMBER_FAILED",
)<AddProjectTeamMemberRequest, undefined, FormError[]>();

export const openProjectTeamAddMemberFormAction = createAction(
  "OPEN_PROJECT_TEAM_ADD_MEMBER_FORM",
)();

export const closeProjectTeamAddMemberFormAction = createAction(
  "CLOSE_PROJECT_TEAM_ADD_MEMBER_FORM",
)();

export const changeProjectTeamStatusAsync = createAsyncAction(
  "CHANGE_PROJECT_TEAM_STATUS_REQUEST",
  "CHANGE_PROJECT_TEAM_STATUS_SUCCESS",
  "CHANGE_PROJECT_TEAM_STATUS_FAILED",
)<ChangeProjectTeamStatusRequest, undefined, FormError[]>();

export const fetchEmployeesAsync = createAsyncAction(
  "FETCH_EMPLOYEES_REQUEST",
  "FETCH_EMPLOYEES_SUCCESS",
  "FETCH_EMPLOYEES_FAILURE",
)<undefined, Paging<EmployeeShort>, string>();
