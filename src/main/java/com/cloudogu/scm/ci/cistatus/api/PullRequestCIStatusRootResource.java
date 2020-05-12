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
import sonia.scm.plugin.Requires;
import sonia.scm.repository.Repository;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;

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

  @Inject
  public PullRequestCIStatusRootResource(CIStatusService ciStatusService, CIStatusMapper mapper, CIStatusCollectionDtoMapper collectionDtoMapper, RepositoryResolver repositoryResolver) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.collectionDtoMapper = collectionDtoMapper;
    this.repositoryResolver = repositoryResolver;
  }

  @Path("{namespace}/{name}/pullrequest/{id}")
  public PullRequestCIStatusResource getPullRequestCIStatusResource(@PathParam("namespace") String namespace, @PathParam("name") String name, @PathParam("id") String pullRequestId) {
    Repository repository = repositoryResolver.resolve(namespace, name);
    return new PullRequestCIStatusResource(ciStatusService, mapper, collectionDtoMapper, repository, pullRequestId);
  }
}
