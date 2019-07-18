package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.google.common.annotations.VisibleForTesting;
import sonia.scm.repository.Repository;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

class CIStatusResource {

  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;
  private final Repository repository;
  private final String changesetId;

  CIStatusResource(CIStatusService ciStatusService, CIStatusMapper mapper, Repository repository, String changesetId) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
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
  @Produces(MediaType.APPLICATION_JSON) // TODO vnd media type
  public List<CIStatusDto> getAll() {
    CIStatusCollection ciStatusCollection = ciStatusService.get(repository, changesetId);
    return ciStatusCollection
      .stream()
      .map(ciStatus -> mapper.map(repository, changesetId, ciStatus))
      .collect(Collectors.toList());
  }

  @GET
  @Path("{type}/{ciName}")
  @Produces(MediaType.APPLICATION_JSON) // TODO vnd media type
  public CIStatusDto get(
    @PathParam("type") String type,
    @PathParam("ciName") String ciName
  ) {
    CIStatusCollection ciStatusCollection = ciStatusService.get(repository, changesetId);
    return mapper.map(repository, changesetId, ciStatusCollection.get(type, ciName));
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON) // TODO vnd media type
  @Path("{type}/{ciName}")
  public Response put(@PathParam("type") String type, @PathParam("ciName") String ciName, CIStatusDto ciStatusDto) {
    if (type != ciStatusDto.getType() || ciName != ciStatusDto.getName()) {
      return Response.status(400).build();
    }
    CIStatus ciStatus = mapper.map(ciStatusDto);
    CIStatusCollection ciStatusCollection = ciStatusService.get(repository, changesetId);
    ciStatusCollection.put(ciStatus);
    ciStatusService.put(repository, changesetId, ciStatusCollection);

    return Response.noContent().build();
  }

}
