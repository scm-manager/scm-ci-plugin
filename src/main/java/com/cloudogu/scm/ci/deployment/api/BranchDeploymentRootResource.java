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

import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.deployment.service.DeploymentService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import sonia.scm.repository.Repository;

import static com.cloudogu.scm.ci.deployment.Constants.DEPLOYMENTS_PATH_V2;

@OpenAPIDefinition(tags = {
  @Tag(name = "CI Plugin", description = "CI plugin provided endpoints")
})
@Path(DEPLOYMENTS_PATH_V2)
public class BranchDeploymentRootResource {

  private final RepositoryResolver repositoryResolver;
  private final DeploymentCollectionMapper deploymentCollectionMapper;
  private final DeploymentService deploymentService;

  @Inject
  public BranchDeploymentRootResource(RepositoryResolver repositoryResolver,
                                      DeploymentCollectionMapper deploymentCollectionMapper,
                                      DeploymentService deploymentService) {
    this.repositoryResolver = repositoryResolver;
    this.deploymentCollectionMapper = deploymentCollectionMapper;
    this.deploymentService = deploymentService;
  }

  @Path("{namespace}/{name}/branches/{branch}")
  public BranchDeploymentResource getBranchDeploymentResource(@PathParam("namespace") String namespace,
                                                              @PathParam("name") String name,
                                                              @PathParam("branch") String branch) {
    Repository repository = repositoryResolver.resolve(namespace, name);
    return new BranchDeploymentResource(repository, branch, deploymentCollectionMapper, deploymentService);
  }
}
