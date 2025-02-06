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

package com.cloudogu.scm.ci.deployment.service;

import com.cloudogu.scm.ci.cistatus.workflow.SourceRevisionResolver;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.pullrequest.service.PullRequestService;
import jakarta.inject.Inject;
import sonia.scm.ContextEntry;
import sonia.scm.plugin.Requires;
import sonia.scm.repository.Repository;

import static sonia.scm.NotFoundException.notFound;

@Requires("scm-review-plugin")
public class DeploymentMerger {

  private final DeploymentService deploymentService;
  private final PullRequestService pullRequestService;
  private final SourceRevisionResolver sourceRevisionResolver;

  @Inject
  public DeploymentMerger(DeploymentService deploymentService, PullRequestService pullRequestService, SourceRevisionResolver sourceRevisionResolver) {
    this.deploymentService = deploymentService;
    this.pullRequestService = pullRequestService;
    this.sourceRevisionResolver = sourceRevisionResolver;
  }

  public DeploymentCollection mergePullRequestDeployments(Repository repository, String pullRequestId) {
    PullRequest pullRequest = this.pullRequestService.get(repository, pullRequestId);
    if (pullRequest == null) {
      throw notFound(ContextEntry.ContextBuilder.entity(PullRequest.class, pullRequestId));
    }

    DeploymentCollection mergedDeployments = new DeploymentCollection();
    sourceRevisionResolver.resolveRevisionOfSource(repository, pullRequest).ifPresent(revision -> {
      DeploymentCollection changesetDeployments = deploymentService.getAllCommitDeployments(repository, revision);
      changesetDeployments.forEach(mergedDeployments::put);
    });

    deploymentService.getAllPullRequestDeployments(
      repository, pullRequestId
    ).forEach(mergedDeployments::put);


    return mergedDeployments;
  }
}
