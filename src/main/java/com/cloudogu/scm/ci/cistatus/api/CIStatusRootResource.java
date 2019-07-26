package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.google.inject.Inject;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;


@Path(CIStatusRootResource.CI_PATH_V2)
public class CIStatusRootResource {

  static final String CI_PATH_V2 = "v2/ci";

  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;
  private final CIStatusCollectionDtoMapper collectionDtoMapper;
  private final RepositoryResolver repositoryResolver;
  private final RepositoryServiceFactory repositoryServiceFactory;

  @Inject
  public CIStatusRootResource(CIStatusService ciStatusService, CIStatusMapper mapper, CIStatusCollectionDtoMapper collectionDtoMapper, RepositoryResolver repositoryResolver, RepositoryServiceFactory repositoryServiceFactory) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.collectionDtoMapper = collectionDtoMapper;
    this.repositoryResolver = repositoryResolver;
    this.repositoryServiceFactory = repositoryServiceFactory;
  }

  @Path("{namespace}/{name}/changesets/{changesetId}")
  public CIStatusResource getCIStatusResource(@PathParam("namespace") String namespace, @PathParam("name") String name, @PathParam("changesetId") String changesetId) throws IOException {
    Repository repository = repositoryResolver.resolve(namespace, name);
    try (RepositoryService repositoryService = repositoryServiceFactory.create(repository)) {
      String resolvedChangesetId = repositoryService.getLogCommand().getChangeset(changesetId).getId();
      return new CIStatusResource(ciStatusService, mapper, collectionDtoMapper, repository, resolvedChangesetId);
    }
  }
}
