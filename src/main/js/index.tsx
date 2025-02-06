/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import { binder, extensionPoints } from "@scm-manager/ui-extensions";
import CIStatusSummary from "./CIStatusSummary";
import CIStatusBar from "./CIStatusBar";
import CIStatusAllSuccessRuleConfiguration from "./workflow/CIStatusAllSuccessRuleConfiguration";
import CIStatusXSuccessRuleConfiguration from "./workflow/CIStatusXSuccessRuleConfiguration";
import CIStatusOfTypeSuccessRuleConfiguration from "./workflow/CIStatusOfTypeSuccessRuleConfiguration";
import CIStatusNamedSuccessRuleConfiguration from "./workflow/CIStatusNamedSuccessRuleConfiguration";
import BranchDetailWrapper from "./BranchDetailWrapper";
import BranchDetailsCIStatusSummary from "./BranchDetailsCIStatusSummary";
import PullRequestDetailsCIStatusSummary from "./PullRequestDetailsCIStatusSummary";
import React from "react";
import BranchDetailsDeploymentStatusSummary from "./BranchDetailsDeploymentStatusSummary";
import ChangesetDeploymentStatusSummary from "./ChangesetDeploymentStatusSummary";
import PullRequestDeploymentStatusSummary from "./PullRequestDeploymentStatusSummary";
import DeploymentStatusBar from "./DeploymentStatusBar";

binder.bind<extensionPoints.ChangesetRight>("changeset.right", props => (
  <span className="ml-2">
    <CIStatusSummary {...props} />
    <ChangesetDeploymentStatusSummary changeset={props.changeset} repository={props.repository} />
  </span>
));
binder.bind<extensionPoints.BranchListDetail>("branches.list.detail", BranchDetailsCIStatusSummary);
binder.bind<extensionPoints.BranchListDetail>("branches.list.detail", BranchDetailsDeploymentStatusSummary);
binder.bind("reviewPlugin.pullrequest.top", CIStatusBar);
binder.bind("reviewPlugin.pullrequest.top", DeploymentStatusBar, {
  predicate: ({ pullRequest }) => pullRequest._links.deploymentStatus
});

binder.bind("reviewPlugin.workflow.config.CIStatusAllSuccessRule", CIStatusAllSuccessRuleConfiguration);
binder.bind("reviewPlugin.workflow.config.CIStatusXSuccessRule", CIStatusXSuccessRuleConfiguration);
binder.bind("reviewPlugin.workflow.config.CIStatusOfTypeSuccessRule", CIStatusOfTypeSuccessRuleConfiguration);
binder.bind("reviewPlugin.workflow.config.CIStatusNamedSuccessRule", CIStatusNamedSuccessRuleConfiguration);

binder.bind("repos.branch-details.information", BranchDetailWrapper);
binder.bind("pull-requests.list.detail", PullRequestDetailsCIStatusSummary, {
  predicate: ({ pullRequest }) => pullRequest._links.ciStatus
});
binder.bind("pull-requests.list.detail", PullRequestDeploymentStatusSummary);
