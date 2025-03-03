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

import com.cloudogu.scm.ci.deployment.service.DeploymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.repository.Repository;
import sonia.scm.web.VndMediaType;

import java.util.Collection;

import static com.cloudogu.scm.ci.deployment.Constants.MEDIA_TYPE;

public class BranchDeploymentResource {

  private final Repository repository;
  private final String branch;
  private final DeploymentCollectionMapper deploymentCollectionMapper;
  private final DeploymentService deploymentService;

  public BranchDeploymentResource(Repository repository,
                                  String branch,
                                  DeploymentCollectionMapper deploymentCollectionMapper,
                                  DeploymentService deploymentService) {
    this.repository = repository;
    this.branch = branch;
    this.deploymentCollectionMapper = deploymentCollectionMapper;
    this.deploymentService = deploymentService;
  }

  @GET
  @Path("")
  @Produces(MEDIA_TYPE)
  @Operation(
    summary = "Get all branch deployments",
    description = "Returns all deployments of the head of a repository branch",
    tags = "CI Plugin",
    operationId = "ci_get_all_branch_deployments"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MEDIA_TYPE,
      schema = @Schema(implementation = DeploymentDto.class)
    )
  )
  @ApiResponse(
    responseCode = "401",
    description = "Not authenticated / invalid credentials"
  )
  @ApiResponse(
    responseCode = "403",
    description = "the current user does not have the required 'read' permission for this repository"
  )
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Collection<DeploymentDto> getAllDeployments() {
    return deploymentCollectionMapper.map(
      repository,
      deploymentService.getAllBranchDeployments(repository, branch)
    );
  }
}
