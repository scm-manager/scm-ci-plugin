/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

binder.bind<extensionPoints.ChangesetRight>("changeset.right", props => (
  <span className="ml-2">
    <CIStatusSummary {...props} />
  </span>
));
binder.bind<extensionPoints.BranchListDetail>("branches.list.detail", BranchDetailsCIStatusSummary);
binder.bind("reviewPlugin.pullrequest.top", CIStatusBar);

binder.bind("reviewPlugin.workflow.config.CIStatusAllSuccessRule", CIStatusAllSuccessRuleConfiguration);
binder.bind("reviewPlugin.workflow.config.CIStatusXSuccessRule", CIStatusXSuccessRuleConfiguration);
binder.bind("reviewPlugin.workflow.config.CIStatusOfTypeSuccessRule", CIStatusOfTypeSuccessRuleConfiguration);
binder.bind("reviewPlugin.workflow.config.CIStatusNamedSuccessRule", CIStatusNamedSuccessRuleConfiguration);

binder.bind("repos.branch-details.information", BranchDetailWrapper);
binder.bind("pull-requests.list.detail", PullRequestDetailsCIStatusSummary, {
  predicate: ({ pullRequest }) => pullRequest._links.ciStatus
});
