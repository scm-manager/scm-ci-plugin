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

import com.cloudogu.scm.ci.PermissionCheck;
import com.google.common.annotations.VisibleForTesting;
import jakarta.inject.Inject;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.InternalRepositoryException;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

import static sonia.scm.ContextEntry.ContextBuilder.entity;
import static sonia.scm.NotFoundException.notFound;

public class DeploymentService {

  private final DataStoreFactory dataStoreFactory;
  private final Clock clock;
  private final RepositoryServiceFactory repositoryServiceFactory;

  @Inject
  public DeploymentService(DataStoreFactory dataStoreFactory, RepositoryServiceFactory repositoryServiceFactory) {
    this(dataStoreFactory, Clock.systemDefaultZone(), repositoryServiceFactory);
  }

  @VisibleForTesting
  DeploymentService(DataStoreFactory dataStoreFactory, Clock clock, RepositoryServiceFactory repositoryServiceFactory) {
    this.dataStoreFactory = dataStoreFactory;
    this.clock = clock;
    this.repositoryServiceFactory = repositoryServiceFactory;
  }

  private DeploymentCollection getDeployments(Repository repository, DeploymentType store, String parentId) {
    PermissionCheck.checkRead(repository);
    return getDeploymentStore(repository, store).getOptional(parentId).orElseGet(DeploymentCollection::new);
  }

  private DataStore<DeploymentCollection> getDeploymentStore(Repository repository, DeploymentType store) {
    return dataStoreFactory.withType(DeploymentCollection.class).withName(store.storeName).forRepository(repository).build();
  }

  public DeploymentCollection getAllCommitDeployments(Repository repository, String commitId) {
    return getDeployments(repository, DeploymentType.COMMIT, commitId);
  }

  public DeploymentCollection getAllPullRequestDeployments(Repository repository, String pullRequestId) {
    return getDeployments(repository, DeploymentType.PULL_REQUEST, pullRequestId);
  }

  public DeploymentCollection getAllBranchDeployments(Repository repository, String branchName) {
    //Explicit permission check here to avoid, having to look for changeset id if not necessary because of permissions
    PermissionCheck.checkRead(repository);
    return getAllCommitDeployments(
      repository,
      tryToGetLatestChangesetIdOfBranch(repository, branchName)
    );
  }

  private String tryToGetLatestChangesetIdOfBranch(Repository repository, String branchName) {
    try (RepositoryService service = repositoryServiceFactory.create(repository)) {
      ChangesetPagingResult changesets = service
        .getLogCommand()
        .setBranch(branchName)
        .setPagingLimit(1)
        .getChangesets();

      if (changesets.getChangesets().isEmpty()) {
        throw notFound(entity("Branch", branchName).in(repository));
      }

      return changesets.getChangesets().get(0).getId();
    } catch (IOException e) {
      throw new InternalRepositoryException(
        entity("Branch", branchName).in(repository).build(),
        String.format("could not read changeset for branch %s in repository %s", branchName, repository),
        e);
    }
  }

  private void putDeployment(Deployment deployment, Repository repository, DeploymentType store, String parentId) {
    PermissionCheck.checkWrite(repository);

    deployment.setDeployedAt(Instant.now(clock));

    DataStore<DeploymentCollection> deploymentStore = getDeploymentStore(repository, store);
    DeploymentCollection deployments = deploymentStore.getOptional(parentId).orElseGet(DeploymentCollection::new);
    deployments.put(deployment);
    deploymentStore.put(parentId, deployments);
  }

  public void putCommitDeployment(Deployment deployment, Repository repository, String commitId) {
    putDeployment(deployment, repository, DeploymentType.COMMIT, commitId);
  }

  public void putPullRequestDeployment(Deployment deployment, Repository repository, String pullRequestId) {
    putDeployment(deployment, repository, DeploymentType.PULL_REQUEST, pullRequestId);
  }

  private void deleteDeployment(DeploymentCollection.Key deploymentId, Repository repository, DeploymentType store, String parentId) {
    PermissionCheck.checkWrite(repository);
    DataStore<DeploymentCollection> deploymentStore = getDeploymentStore(repository, store);
    DeploymentCollection deployments = deploymentStore.getOptional(parentId).orElseGet(DeploymentCollection::new);
    deployments.remove(deploymentId);
    deploymentStore.put(parentId, deployments);
  }

  public void deleteCommitDeployment(DeploymentCollection.Key deploymentId, Repository repository, String commitId) {
    deleteDeployment(deploymentId, repository, DeploymentType.COMMIT, commitId);
  }

  public void deletePullRequestDeployment(DeploymentCollection.Key deploymentId, Repository repository, String pullRequestId) {
    deleteDeployment(deploymentId, repository, DeploymentType.PULL_REQUEST, pullRequestId);
  }
}
