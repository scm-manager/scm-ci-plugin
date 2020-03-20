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
package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.google.common.annotations.VisibleForTesting;
import de.otto.edison.hal.HalRepresentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import sonia.scm.ContextEntry;
import sonia.scm.IllegalIdentifierChangeException;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.repository.Repository;
import sonia.scm.web.VndMediaType;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

public class CIStatusResource {

  private static final String MEDIA_TYPE = "application/vnd.scmm-cistatus+json;v=2";

  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;
  private final CIStatusCollectionDtoMapper collectionDtoMapper;
  private final Repository repository;
  private final String changesetId;

  CIStatusResource(CIStatusService ciStatusService, CIStatusMapper mapper, CIStatusCollectionDtoMapper collectionDtoMapper, Repository repository, String changesetId) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.collectionDtoMapper = collectionDtoMapper;
    this.repository = repository;
    this.changesetId = changesetId;
  }

  @VisibleForTesting
  Repository getRepository() {
    return repository;
  }

  @VisibleForTesting
  String getChangesetId() {
    return changesetId;
  }

  @GET
  @Produces(MEDIA_TYPE)
  @Path("")
  @Operation(
    summary = "Get all ci status",
    description = "Returns all ci status for a changeset.",
    tags = "CI Plugin",
    operationId = "ci_get_all_status"
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
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the \"readCIStatus\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public HalRepresentation getAll() {
    CIStatusCollection ciStatusCollection = ciStatusService.get(repository, changesetId);
    return collectionDtoMapper.map(ciStatusCollection.stream(), repository, changesetId);
  }

  @GET
  @Path("{type}/{ciName}")
  @Produces(MEDIA_TYPE)
  @Operation(
    summary = "Get single ci status",
    description = "Returns single ci status for a changeset.",
    tags = "CI Plugin",
    operationId = "ci_get_single_status"
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
  @ApiResponse(responseCode = "403", description = "not authorized / the current user does not have the \"readCIStatus\" privilege")
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
    CIStatusCollection ciStatusCollection = ciStatusService.get(repository, changesetId);
    return mapper.map(repository, changesetId, ciStatusCollection.get(type, ciName));
  }

  @PUT
  @Consumes(MEDIA_TYPE)
  @Path("{type}/{ciName}")
  @Operation(
    summary = "Update ci status",
    description = "Updates single ci status.",
    tags = "CI Plugin",
    operationId = "ci_put_status"
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
    if (!type.equals(ciStatusDto.getType()) || !ciName.equals(ciStatusDto.getName())) {
      throw new IllegalIdentifierChangeException(ContextEntry.ContextBuilder.entity(CIStatusDto.class,
        ciStatusDto.getName() + ":" + ciStatusDto.getType()), "changing identifier attributes is not allowed");
    }
    CIStatus ciStatus = mapper.map(ciStatusDto);
    ciStatusService.put(repository, changesetId, ciStatus);

    return Response.noContent().build();
  }
}
