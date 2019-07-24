package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.google.inject.Inject;
import sonia.scm.repository.Repository;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;


@Path(CIStatusRootResource.CI_PATH_V2)
public class CIStatusRootResource {

  static final String CI_PATH_V2 = "v2/ci";

  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;
  private final CIStatusCollectionDtoMapper collectionDtoMapper;
  private final RepositoryResolver repositoryResolver;

  @Inject
  public CIStatusRootResource(CIStatusService ciStatusService, CIStatusMapper mapper, CIStatusCollectionDtoMapper collectionDtoMapper, RepositoryResolver repositoryResolver) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.collectionDtoMapper = collectionDtoMapper;
    this.repositoryResolver = repositoryResolver;
  }

  @Path("{namespace}/{name}/changesets/{changeSetId}")
  public CIStatusResource getCIStatusResource(@PathParam("namespace") String namespace, @PathParam("name") String name, @PathParam("changeSetId") String changeSetId) {
    Repository repository = repositoryResolver.resolve(namespace, name);
    return new CIStatusResource(ciStatusService, mapper, collectionDtoMapper, repository, changeSetId);
  }
}
