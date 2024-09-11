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

import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import sonia.scm.ContextEntry;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import java.io.IOException;

import static com.cloudogu.scm.ci.cistatus.Constants.CI_PATH_V2;
import static sonia.scm.NotFoundException.notFound;

@OpenAPIDefinition(tags = {
  @Tag(name = "CI Plugin", description = "CI plugin provided endpoints")
})
@Path(CI_PATH_V2)
public class ChangesetCIStatusRootResource {

  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;
  private final CIStatusCollectionDtoMapper collectionDtoMapper;
  private final RepositoryResolver repositoryResolver;
  private final RepositoryServiceFactory repositoryServiceFactory;

  @Inject
  public ChangesetCIStatusRootResource(CIStatusService ciStatusService, CIStatusMapper mapper, CIStatusCollectionDtoMapper collectionDtoMapper, RepositoryResolver repositoryResolver, RepositoryServiceFactory repositoryServiceFactory) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.collectionDtoMapper = collectionDtoMapper;
    this.repositoryResolver = repositoryResolver;
    this.repositoryServiceFactory = repositoryServiceFactory;
  }

  @Path("{namespace}/{name}/changesets/{changesetId}")
  public ChangesetCIStatusResource getChangesetCIStatusResource(@PathParam("namespace") String namespace, @PathParam("name") String name, @PathParam("changesetId") String changesetId) throws IOException {
    Repository repository = repositoryResolver.resolve(namespace, name);
    try (RepositoryService repositoryService = repositoryServiceFactory.create(repository)) {
      Changeset changeset = repositoryService.getLogCommand().getChangeset(changesetId);
      if (changeset == null) {
        throw notFound(ContextEntry.ContextBuilder.entity("Revision", changesetId).in(repository));
      }
      String resolvedChangesetId = changeset.getId();
      return new ChangesetCIStatusResource(ciStatusService, mapper, collectionDtoMapper, repository, resolvedChangesetId);
    }
  }
}
