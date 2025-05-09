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

import com.cloudogu.scm.ci.deployment.service.DeploymentCollection;
import com.cloudogu.scm.ci.deployment.service.DeploymentService;
import com.cloudogu.scm.ci.deployment.service.DeploymentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.repository.Repository;
import sonia.scm.web.VndMediaType;

import java.util.Collection;

import static com.cloudogu.scm.ci.deployment.Constants.MEDIA_TYPE;

public class ChangesetDeploymentResource {

  private final Repository repository;
  private final String changesetId;
  private final DeploymentMapper deploymentMapper;
  private final DeploymentCollectionMapper deploymentCollectionMapper;
  private final DeploymentService deploymentService;

  public ChangesetDeploymentResource(Repository repository,
                                     String changesetId,
                                     DeploymentMapper deploymentMapper,
                                     DeploymentCollectionMapper deploymentCollectionMapper,
                                     DeploymentService deploymentService) {
    this.repository = repository;
    this.changesetId = changesetId;
    this.deploymentMapper = deploymentMapper;
    this.deploymentCollectionMapper = deploymentCollectionMapper;
    this.deploymentService = deploymentService;
  }

  @GET
  @Path("")
  @Produces(MEDIA_TYPE)
  @Operation(
    summary = "Get all changeset deployments",
    description = "Returns all deployments of a repository changeset",
    tags = "CI Plugin",
    operationId = "ci_get_all_changeset_deployments"
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
      deploymentService.getAllCommitDeployments(repository, changesetId)
    );
  }

  @PUT
  @Path("")
  @Consumes(MEDIA_TYPE)
  @Operation(
    summary = "Update a deployment for a changeset",
    description = "Updates a deployment for the changeset of a repository",
    tags = "CI Plugin",
    operationId = "ci_update_changeset_deployment"
  )
  @ApiResponse(
    responseCode = "204",
    description = "success"
  )
  @ApiResponse(
    responseCode = "400",
    description = "Invalid request body"
  )
  @ApiResponse(
    responseCode = "401",
    description = "Not authenticated / invalid credentials"
  )
  @ApiResponse(
    responseCode = "403",
    description = "The current user does not have the required 'writeCIStatus' permission"
  )
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public void putDeployment(@Valid DeploymentDto dto) {
    deploymentService.putCommitDeployment(
      deploymentMapper.map(DeploymentType.COMMIT, changesetId, dto),
      repository,
      changesetId
    );
  }

  @DELETE
  @Path("{source}/{environment}")
  @Operation(
    summary = "Delete a deployment for a changeset",
    description = "Deletes a deployment for the changeset of a repository",
    tags = "CI Plugin",
    operationId = "ci_delete_changeset_deployment"
  )
  @ApiResponse(
    responseCode = "204",
    description = "success"
  )
  @ApiResponse(
    responseCode = "401",
    description = "Not authenticated / invalid credentials"
  )
  @ApiResponse(
    responseCode = "403",
    description = "The current user does not have the required 'writeCIStatus' permission"
  )
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public void deleteDeployment(@PathParam("source") String source, @PathParam("environment") String environment) {
    deploymentService.deleteCommitDeployment(
      new DeploymentCollection.Key(source, environment),
      repository,
      changesetId
    );
  }
}
