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

package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusMerger;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import de.otto.edison.hal.HalRepresentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.repository.Repository;
import sonia.scm.web.VndMediaType;

import static com.cloudogu.scm.ci.cistatus.Constants.MEDIA_TYPE;
import static com.cloudogu.scm.ci.cistatus.api.CIStatusUtil.validateCIStatus;

public class PullRequestCIStatusResource {

  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;
  private final CIStatusCollectionDtoMapper collectionDtoMapper;
  private final CIStatusMerger ciStatusMerger;
  private final Repository repository;
  private final String pullRequestId;

  PullRequestCIStatusResource(CIStatusService ciStatusService, CIStatusMapper mapper, CIStatusCollectionDtoMapper collectionDtoMapper, CIStatusMerger ciStatusMerger, Repository repository, String pullRequestId) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.collectionDtoMapper = collectionDtoMapper;
    this.ciStatusMerger = ciStatusMerger;
    this.repository = repository;
    this.pullRequestId = pullRequestId;
  }

  @GET
  @Produces(MEDIA_TYPE)
  @Path("")
  @Operation(
    summary = "Get all ci status",
    description = "Returns all ci status for a pull request.",
    tags = "CI Plugin",
    operationId = "ci_get_all_status_for_pull_request"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MEDIA_TYPE,
      schema = @Schema(implementation = HalRepresentation.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the \"read\" privilege for this repository")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public HalRepresentation getAll() {
    CIStatusCollection ciStatusCollection = ciStatusMerger.mergePullRequestCIStatuses(repository, pullRequestId);
    return collectionDtoMapper.map(ciStatusCollection.stream(), repository, pullRequestId);
  }

  @GET
  @Path("{type}/{ciName}")
  @Produces(MEDIA_TYPE)
  @Operation(
    summary = "Get single ci status",
    description = "Returns single ci status for a pull request.",
    tags = "CI Plugin",
    operationId = "ci_get_single_status_for_pull_request"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MEDIA_TYPE,
      schema = @Schema(implementation = CIStatusDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the \"read\" privilege for this repository")
  @ApiResponse(responseCode = "404", description = "not found / ci status by given name not available")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )

  public CIStatusDto get(@PathParam("type") String type, @PathParam("ciName") String ciName) {
    CIStatusCollection ciStatusCollection = ciStatusService.get(CIStatusStore.PULL_REQUEST_STORE, repository, pullRequestId);
    return mapper.map(repository, pullRequestId, ciStatusCollection.get(type, ciName));
  }

  @PUT
  @Consumes(MEDIA_TYPE)
  @Path("{type}/{ciName}")
  @Operation(
    summary = "Update pull request ci status",
    description = "Updates single ci status for pull request.",
    tags = "CI Plugin",
    operationId = "ci_put_status_for_pull_request"
  )
  @ApiResponse(responseCode = "204", description = "update success")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the \"writeCIStatus\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response put(@PathParam("type") String type, @PathParam("ciName") String ciName, @Valid CIStatusDto ciStatusDto) {
    validateCIStatus(type, ciName, ciStatusDto);
    CIStatus ciStatus = mapper.map(ciStatusDto);
    ciStatusService.put(CIStatusStore.PULL_REQUEST_STORE, repository, pullRequestId, ciStatus);

    return Response.noContent().build();
  }
}
