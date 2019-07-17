package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.PermissionCheck;
import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.google.inject.Inject;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

public class CIStatusResource {

    private final CIStatusService ciStatusService;
    private final CIStatusMapper mapper;
    private final RepositoryResolver repositoryResolver;

    @Inject
    public CIStatusResource(CIStatusService ciStatusService, CIStatusMapper mapper, RepositoryResolver repositoryResolver) {
        this.ciStatusService = ciStatusService;
        this.mapper = mapper;
        this.repositoryResolver = repositoryResolver;
    }

  @GET
  @Path("{type}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(@Context UriInfo uriInfo, @PathParam("namespace") String namespace, @PathParam("name") String name, @PathParam("changeSetId") String changeSetId, @PathParam("type") String type) {
    Repository repository = repositoryResolver.resolve(new NamespaceAndName(namespace, name));
    PermissionCheck.checkRead(repository);
//    CIStatus ciStatus = ciStatusService.get(repository, changeSetId, type);
//    return Response.ok(mapper.map(repository, changeSetId, ciStatus)).build();
    return null;
  }

  @GET
  @Path("{type}/{ciName}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(@Context UriInfo uriInfo, @PathParam("namespace") String namespace, @PathParam("name") String name, @PathParam("changeSetId") String changeSetId, @PathParam("type") String type, @PathParam("ciName") String ciName) {
    Repository repository = repositoryResolver.resolve(new NamespaceAndName(namespace, name));
    PermissionCheck.checkRead(repository);
    List<CIStatus> ciStatus = ciStatusService.get(repository, changeSetId, type)
      .stream()
      .filter(ciStatus1 -> ciStatus1.getName().equals(ciName))
      .collect(Collectors.toList());
//    return Response.ok(mapper.map(repository, changeSetId, ciStatus)).build();
    return null;
  }
}
