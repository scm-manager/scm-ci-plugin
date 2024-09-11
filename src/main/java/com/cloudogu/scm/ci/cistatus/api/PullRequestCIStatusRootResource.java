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
import com.cloudogu.scm.ci.cistatus.service.CIStatusMerger;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import sonia.scm.plugin.Requires;
import sonia.scm.repository.Repository;

import static com.cloudogu.scm.ci.cistatus.Constants.CI_PATH_V2;

@OpenAPIDefinition(tags = {
  @Tag(name = "CI Plugin", description = "CI plugin provided endpoints")
})
@Requires("scm-review-plugin")
@Path(CI_PATH_V2)
public class PullRequestCIStatusRootResource {

  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;
  private final CIStatusCollectionDtoMapper collectionDtoMapper;
  private final RepositoryResolver repositoryResolver;
  private final CIStatusMerger ciStatusMerger;

  @Inject
  public PullRequestCIStatusRootResource(CIStatusService ciStatusService, CIStatusMapper mapper, CIStatusCollectionDtoMapper collectionDtoMapper, RepositoryResolver repositoryResolver, CIStatusMerger ciStatusMerger) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.collectionDtoMapper = collectionDtoMapper;
    this.repositoryResolver = repositoryResolver;
    this.ciStatusMerger = ciStatusMerger;
  }

  @Path("{namespace}/{name}/pullrequest/{id}")
  public PullRequestCIStatusResource getPullRequestCIStatusResource(@PathParam("namespace") String namespace, @PathParam("name") String name, @PathParam("id") String pullRequestId) {
    Repository repository = repositoryResolver.resolve(namespace, name);
    return new PullRequestCIStatusResource(ciStatusService, mapper, collectionDtoMapper, ciStatusMerger, repository, pullRequestId);
  }
}
