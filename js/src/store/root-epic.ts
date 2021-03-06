import { combineEpics } from "redux-observable";

import * as app from "../features/app/epics";
import * as auth from "../features/auth/epics";
import * as humanResource from "../features/human-resouce/epics";
import * as account from "../features/account/epics";
import * as location from "../features/location/epics";
import * as project from "../features/project/epics";
import * as notification from "../features/notification/epics";

export default combineEpics(
  ...Object.values(app),
  ...Object.values(auth),
  ...Object.values(humanResource),
  ...Object.values(account),
  ...Object.values(location),
  ...Object.values(project),
  ...Object.values(notification),
);
