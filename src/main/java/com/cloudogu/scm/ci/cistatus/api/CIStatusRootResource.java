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

import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;

@OpenAPIDefinition(tags = {
  @Tag(name = "CI Plugin", description = "CI plugin provided endpoints")
})
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
