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

package com.cloudogu.scm.ci.cistatus.service;

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.workflow.SourceRevisionResolver;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.pullrequest.service.PullRequestService;
import jakarta.inject.Inject;
import sonia.scm.plugin.Requires;
import sonia.scm.repository.Repository;

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
    return pullRequestCIStatusCollection.get(status.getType(), status.getName()) == null &&
            pullRequestCIStatusCollection.stream().noneMatch(ciStatus -> status.getType().equals(ciStatus.getType()) && status.getName().equals(ciStatus.getReplaces()));
  }
}
