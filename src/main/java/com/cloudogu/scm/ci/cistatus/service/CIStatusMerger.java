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

package com.cloudogu.scm.ci.cistatus.service;

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.workflow.SourceRevisionResolver;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.pullrequest.service.PullRequestService;
import sonia.scm.plugin.Requires;
import sonia.scm.repository.Repository;

import javax.inject.Inject;

@Requires("scm-review-plugin")
public class CIStatusMerger {

  private final CIStatusService statusService;
  private final PullRequestService pullRequestService;
  private final SourceRevisionResolver sourceRevisionResolver;

  @Inject
  public CIStatusMerger(CIStatusService statusService, PullRequestService pullRequestService, SourceRevisionResolver sourceRevisionResolver) {
    this.statusService = statusService;
    this.pullRequestService = pullRequestService;
    this.sourceRevisionResolver = sourceRevisionResolver;
  }

  public CIStatusCollection mergePullRequestCIStatuses(Repository repository, String pullRequestId) {
    PullRequest pullRequest = pullRequestService.get(repository, pullRequestId);

    CIStatusCollection mergedCIStatusCollection = new CIStatusCollection();
    sourceRevisionResolver.resolveRevisionOfSource(repository, pullRequest)
      .ifPresent(changesetId -> {
        CIStatusCollection pullRequestCIStatusCollection = statusService.get(CIStatusStore.PULL_REQUEST_STORE, repository, pullRequestId);
        pullRequestCIStatusCollection.stream().forEach(mergedCIStatusCollection::put);

        CIStatusCollection changesetCIStatusCollection = statusService.get(CIStatusStore.CHANGESET_STORE, repository, changesetId);
        for (CIStatus status : changesetCIStatusCollection) {
          if (isStatusNotContainedInPullRequestStatus(mergedCIStatusCollection, status)) {
            mergedCIStatusCollection.put(status);
          }
        }
      });

    return mergedCIStatusCollection;
  }

  private boolean isStatusNotContainedInPullRequestStatus(CIStatusCollection pullRequestCIStatusCollection, CIStatus status) {
    return pullRequestCIStatusCollection.get(status.getType(), status.getName()) == null;
  }
}
