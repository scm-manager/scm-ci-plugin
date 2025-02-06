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

package com.cloudogu.scm.ci.deployment.api;

import com.cloudogu.scm.ci.deployment.service.Deployment;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.Repository;

public class DeploymentStatusPathBuilder {

  private static final String GET_ALL_DEPLOYMENTS = "getAllDeployments";
  private static final String GET_PULL_REQUEST_DEPLOYMENT_RESOURCE = "getPullRequestDeploymentResource";
  private static final String GET_CHANGESET_DEPLOYMENT_RESOURCE = "getChangesetDeploymentResource";
  private static final String GET_BRANCH_DEPLOYMENT_RESOURCE = "getBranchDeploymentResource";
  private static final String DELETE_DEPLOYMENT = "deleteDeployment";
  private static final String PUT_DEPLOYMENT = "putDeployment";

  private final Provider<ScmPathInfoStore> pathInfoStore;

  @Inject
  public DeploymentStatusPathBuilder(Provider<ScmPathInfoStore> pathInfoStore) {
    this.pathInfoStore = pathInfoStore;
  }

  public String createGetBranchDeploymentsLink(Repository repository, String branchName) {
    LinkBuilder linkBuilder = new LinkBuilder(
      pathInfoStore.get().get(),
      BranchDeploymentRootResource.class,
      BranchDeploymentResource.class
    );
    return linkBuilder
      .method(GET_BRANCH_DEPLOYMENT_RESOURCE)
      .parameters(repository.getNamespace(), repository.getName(), branchName)
      .method(GET_ALL_DEPLOYMENTS)
      .parameters()
      .href();
  }

  public String createGetPullRequestDeploymentsLink(Repository repository, String pullRequestId) {
    LinkBuilder linkBuilder = new LinkBuilder(
      pathInfoStore.get().get(),
      PullRequestDeploymentRootResource.class,
      PullRequestDeploymentResource.class
    );
    return linkBuilder
      .method(GET_PULL_REQUEST_DEPLOYMENT_RESOURCE)
      .parameters(repository.getNamespace(), repository.getName(), pullRequestId)
      .method(GET_ALL_DEPLOYMENTS)
      .parameters()
      .href();
  }

  public String createGetChangesetDeploymentsLink(Repository repository, String changesetId) {
    LinkBuilder linkBuilder = new LinkBuilder(
      pathInfoStore.get().get(),
      ChangesetDeploymentRootResource.class,
      ChangesetDeploymentResource.class
    );
    return linkBuilder
      .method(GET_CHANGESET_DEPLOYMENT_RESOURCE)
      .parameters(repository.getNamespace(), repository.getName(), changesetId)
      .method(GET_ALL_DEPLOYMENTS)
      .parameters()
      .href();
  }

  public String createDeleteChangesetDeploymentLink(Repository repository, String changesetId, Deployment deployment) {
    LinkBuilder linkBuilder = new LinkBuilder(
      pathInfoStore.get().get(),
      ChangesetDeploymentRootResource.class,
      ChangesetDeploymentResource.class
    );
    return linkBuilder
      .method(GET_CHANGESET_DEPLOYMENT_RESOURCE)
      .parameters(repository.getNamespace(), repository.getName(), changesetId)
      .method(DELETE_DEPLOYMENT)
      .parameters(deployment.getSource(), deployment.getEnvironment())
      .href();
  }

  public String createDeletePullRequestDeploymentLink(Repository repository, String pullRequestId, Deployment deployment) {
    LinkBuilder linkBuilder = new LinkBuilder(
      pathInfoStore.get().get(),
      PullRequestDeploymentRootResource.class,
      PullRequestDeploymentResource.class
    );
    return linkBuilder
      .method(GET_PULL_REQUEST_DEPLOYMENT_RESOURCE)
      .parameters(repository.getNamespace(), repository.getName(), pullRequestId)
      .method(DELETE_DEPLOYMENT)
      .parameters(deployment.getSource(), deployment.getEnvironment())
      .href();
  }

  public String createUpdateChangesetDeploymentStatusLink(Repository repository, String changesetId) {
    LinkBuilder linkBuilder = new LinkBuilder(
      pathInfoStore.get().get(),
      ChangesetDeploymentRootResource.class,
      ChangesetDeploymentResource.class
    );
    return linkBuilder
      .method(GET_CHANGESET_DEPLOYMENT_RESOURCE)
      .parameters(repository.getNamespace(), repository.getName(), changesetId)
      .method(PUT_DEPLOYMENT)
      .parameters()
      .href();
  }

  public String createUpdatePullRequestDeploymentStatusLink(Repository repository, String pullRequestId) {
    LinkBuilder linkBuilder = new LinkBuilder(
      pathInfoStore.get().get(),
      PullRequestDeploymentRootResource.class,
      PullRequestDeploymentResource.class
    );
    return linkBuilder
      .method(GET_PULL_REQUEST_DEPLOYMENT_RESOURCE)
      .parameters(repository.getNamespace(), repository.getName(), pullRequestId)
      .method(PUT_DEPLOYMENT)
      .parameters()
      .href();
  }
}
