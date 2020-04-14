import { routerActions as router } from "connected-react-router";
import * as auth from "../features/auth/actions";
import * as humanResource from "../features/human-resouce/actions";
import * as account from "../features/account/actions";
import * as location from "../features/human-location/actions";
import * as project from "../features/project/actions";


export default {
  router,
  auth,
  humanResource,
  account,
  location,
  project
};
