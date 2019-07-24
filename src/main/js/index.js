// @flow
import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import CIStatusSummary from "./CIStatusSummary";
import CIStatusModalView from "./CIStatusModalView";

binder.bind("changeset.right", CIStatusSummary);

