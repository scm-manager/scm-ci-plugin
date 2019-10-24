import { binder } from "@scm-manager/ui-extensions";
import CIStatusSummary from "./CIStatusSummary";
import CIStatusBar from "./CIStatusBar";

binder.bind("changeset.right", CIStatusSummary);
binder.bind("reviewPlugin.pullrequest.top", CIStatusBar);
