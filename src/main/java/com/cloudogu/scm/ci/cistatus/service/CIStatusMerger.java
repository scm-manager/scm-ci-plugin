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

import com.cloudogu.scm.ci.cistatus.workflow.SourceRevisionResolver;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.pullrequest.service.PullRequestService;
import sonia.scm.plugin.Requires;
import sonia.scm.repository.Repository;

import javax.inject.Inject;

import static com.cloudogu.scm.ci.cistatus.Constants.CHANGESET_STORE_NAME;
import static com.cloudogu.scm.ci.cistatus.Constants.PULL_REQUEST_STORE_NAME;

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
    String changesetId = sourceRevisionResolver.resolve(repository, pullRequest.getSource());

    CIStatusCollection changesetCIStatusCollection = statusService.get(CHANGESET_STORE_NAME, repository, changesetId);
    CIStatusCollection pullRequestCIStatusCollection = statusService.get(PULL_REQUEST_STORE_NAME, repository, pullRequestId);

    for (CIStatus status : changesetCIStatusCollection) {
      CIStatus prCIStatus = pullRequestCIStatusCollection.get(status.getType(), status.getName());
      if (prCIStatus == null) {
        pullRequestCIStatusCollection.put(status);
      }
    }

    return pullRequestCIStatusCollection;
  }
}
