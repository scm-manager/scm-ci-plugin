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
  private final RepositoryResolver repositoryResolver;
  private final RepositoryServiceFactory repositoryServiceFactory;

  @Inject
  public CIStatusRootResource(CIStatusService ciStatusService, CIStatusMapper mapper, RepositoryResolver repositoryResolver, RepositoryServiceFactory repositoryServiceFactory) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.repositoryResolver = repositoryResolver;
    this.repositoryServiceFactory = repositoryServiceFactory;
  }

  @Path("{namespace}/{name}/changesets/{changeSetId}")
  public CIStatusResource getCIStatusResource(@PathParam("namespace") String namespace, @PathParam("name") String name, @PathParam("changeSetId") String changeSetId) throws IOException {
    Repository repository = repositoryResolver.resolve(namespace, name);
    RepositoryService repositoryService = repositoryServiceFactory.create(repository);
    String resolvedChangeSetId = repositoryService.getLogCommand().getChangeset(changeSetId).getId();
    return new CIStatusResource(ciStatusService, mapper, repository, resolvedChangeSetId);
  }
}
