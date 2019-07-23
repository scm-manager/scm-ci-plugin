// @flow
import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import CIStatusSummary from "./CIStatusSummary";
import CIStatusModalView from "./CIStatusModalView";
import CIStatusBar from "./CIStatusBar";

binder.bind("changeset.information", CIStatusModalView);
binder.bind("changeset.right", CIStatusSummary);
binder.bind("changeset.status", CIStatusBar)
