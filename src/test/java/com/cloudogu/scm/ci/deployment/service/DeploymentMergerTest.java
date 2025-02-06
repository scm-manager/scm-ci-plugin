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
import com.cloudogu.scm.ci.deployment.DeploymentTestData;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.pullrequest.service.PullRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.NotFoundException;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeploymentMergerTest {
  private final Repository repository = RepositoryTestData.createHeartOfGold();
  private final PullRequest pullRequest = new PullRequest("1337", "develop", "main");
  private final String changesetId = "1337";

  @Mock
  private DeploymentService deploymentService;

  @Mock
  private SourceRevisionResolver sourceRevisionResolver;

  @Mock
  private PullRequestService pullRequestService;

  @InjectMocks
  private DeploymentMerger deploymentMerger;

  @BeforeEach
  void setup() {
    lenient().when(pullRequestService.get(repository, pullRequest.getId())).thenReturn(pullRequest);
    lenient().when(sourceRevisionResolver.resolveRevisionOfSource(repository, pullRequest)).thenReturn(Optional.of(changesetId));
  }

  @Test
  void shouldHandlePullRequestNotFound() {
    when(pullRequestService.get(repository, pullRequest.getId())).thenReturn(null);
    assertThatThrownBy(() -> deploymentMerger.mergePullRequestDeployments(repository, pullRequest.getId()))
      .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldOnlyReturnPullRequestDeploymentsBecauseChangesetHasNoDeployments() {
    Deployment windowsDeployment = DeploymentTestData.buildDefaultDeployment(
      DeploymentType.PULL_REQUEST, "ArgoCD", "Windows"
    );
    Deployment linuxDeployment = DeploymentTestData.buildDefaultDeployment(
      DeploymentType.PULL_REQUEST, "ArgoCD", "Linux"
    );
    DeploymentCollection pullRequestDeployments = new DeploymentCollection();
    pullRequestDeployments.put(windowsDeployment);
    pullRequestDeployments.put(linuxDeployment);

    DeploymentCollection changesetDeployments = new DeploymentCollection();

    setupDeploymentService(pullRequestDeployments, changesetDeployments);

    DeploymentCollection mergedCollection = deploymentMerger.mergePullRequestDeployments(repository, pullRequest.getId());

    assertThat(mergedCollection).usingRecursiveComparison().isEqualTo(pullRequestDeployments);
  }

  private void setupDeploymentService(DeploymentCollection pullRequestDeployments, DeploymentCollection changesetDeployments) {
    when(deploymentService.getAllPullRequestDeployments(repository, pullRequest.getId())).thenReturn(pullRequestDeployments);
    when(deploymentService.getAllCommitDeployments(repository, changesetId)).thenReturn(changesetDeployments);
  }

  @Test
  void shouldOnlyReturnChangesetDeploymentsBecausePullRequestHasNoDeployments() {
    Deployment windowsDeployment = DeploymentTestData.buildDefaultDeployment(
      DeploymentType.COMMIT, "ArgoCD", "Windows"
    );
    Deployment linuxDeployment = DeploymentTestData.buildDefaultDeployment(
      DeploymentType.COMMIT, "ArgoCD", "Linux"
    );
    DeploymentCollection changesetDeployments = new DeploymentCollection();
    changesetDeployments.put(windowsDeployment);
    changesetDeployments.put(linuxDeployment);

    DeploymentCollection pullRequestDeployments = new DeploymentCollection();

    setupDeploymentService(pullRequestDeployments, changesetDeployments);

    DeploymentCollection mergedCollection = deploymentMerger.mergePullRequestDeployments(repository, pullRequest.getId());

    assertThat(mergedCollection).usingRecursiveComparison().isEqualTo(changesetDeployments);
  }

  @Test
  void shouldIncludeChangesetDeploymentsBecauseOfDifferentSource() {
    Deployment argoWindowsPrDeployment = DeploymentTestData.buildDefaultDeployment(
      DeploymentType.PULL_REQUEST, "ArgoCD", "Windows"
    );
    DeploymentCollection pullRequestDeployments = new DeploymentCollection();
    pullRequestDeployments.put(argoWindowsPrDeployment);

    Deployment vercelWindowsChangesetDeployment = DeploymentTestData.buildDefaultDeployment(
      DeploymentType.COMMIT, "Vercel", "Windows"
    );
    DeploymentCollection changesetDeployments = new DeploymentCollection();
    changesetDeployments.put(vercelWindowsChangesetDeployment);

    setupDeploymentService(pullRequestDeployments, changesetDeployments);

    DeploymentCollection mergedCollection = deploymentMerger.mergePullRequestDeployments(repository, pullRequest.getId());

    DeploymentCollection expectedResult = new DeploymentCollection();
    expectedResult.put(argoWindowsPrDeployment);
    expectedResult.put(vercelWindowsChangesetDeployment);
    assertThat(mergedCollection).usingRecursiveComparison().isEqualTo(expectedResult);
  }

  @Test
  void shouldIncludeChangesetDeploymentsBecauseOfDifferentEnvironment() {
    Deployment argoWindowsPrDeployment = DeploymentTestData.buildDefaultDeployment(
      DeploymentType.PULL_REQUEST, "ArgoCD", "Windows"
    );
    DeploymentCollection pullRequestDeployments = new DeploymentCollection();
    pullRequestDeployments.put(argoWindowsPrDeployment);

    Deployment argoLinuxChangesetDeployment = DeploymentTestData.buildDefaultDeployment(
      DeploymentType.COMMIT, "ArgoCD", "Linux"
    );
    DeploymentCollection changesetDeployments = new DeploymentCollection();
    changesetDeployments.put(argoLinuxChangesetDeployment);

    setupDeploymentService(pullRequestDeployments, changesetDeployments);

    DeploymentCollection mergedCollection = deploymentMerger.mergePullRequestDeployments(repository, pullRequest.getId());

    DeploymentCollection expectedResult = new DeploymentCollection();
    expectedResult.put(argoWindowsPrDeployment);
    expectedResult.put(argoLinuxChangesetDeployment);
    assertThat(mergedCollection).usingRecursiveComparison().isEqualTo(expectedResult);
  }

  @Test
  void shouldOnlyIncludePullRequestDeploymentsBecauseChangesetDeploymentsHaveSameSourceAndEnvironment() {
    Deployment argoLinuxPrDeployment = DeploymentTestData.buildDefaultDeployment(
      DeploymentType.PULL_REQUEST, "ArgoCD", "Linux"
    );
    DeploymentCollection pullRequestDeployments = new DeploymentCollection();
    pullRequestDeployments.put(argoLinuxPrDeployment);

    Deployment argoLinuxChangesetDeployment = DeploymentTestData.buildDefaultDeployment(
      DeploymentType.COMMIT, "ArgoCD", "Linux"
    );
    DeploymentCollection changesetDeployments = new DeploymentCollection();
    changesetDeployments.put(argoLinuxChangesetDeployment);

    setupDeploymentService(pullRequestDeployments, changesetDeployments);

    DeploymentCollection mergedCollection = deploymentMerger.mergePullRequestDeployments(repository, pullRequest.getId());

    DeploymentCollection expectedResult = new DeploymentCollection();
    expectedResult.put(argoLinuxPrDeployment);
    assertThat(mergedCollection).usingRecursiveComparison().isEqualTo(expectedResult);
  }
}
