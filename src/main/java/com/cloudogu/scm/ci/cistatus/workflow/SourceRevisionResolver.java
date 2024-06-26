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

package com.cloudogu.scm.ci.cistatus.workflow;

import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import jakarta.inject.Inject;
import sonia.scm.ContextEntry;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.InternalRepositoryException;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import java.io.IOException;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static sonia.scm.ContextEntry.ContextBuilder.entity;

public class SourceRevisionResolver {

  private final RepositoryServiceFactory repositoryServiceFactory;

  @Inject
  public SourceRevisionResolver(RepositoryServiceFactory repositoryServiceFactory) {
    this.repositoryServiceFactory = repositoryServiceFactory;
  }

  public Optional<String> resolveRevisionOfSource(Repository repository, PullRequest pullRequest) {
    if (pullRequest.getSourceRevision() != null) {
      return of(pullRequest.getSourceRevision());
    }
    String source = pullRequest.getSource();
    try (RepositoryService repositoryService = repositoryServiceFactory.create(repository)) {
      Changeset changeset = repositoryService.getLogCommand().getChangeset(source);
      if (changeset == null) {
        return empty();
      }
      return of(changeset.getId());
    } catch (IOException ex) {
      throw new InternalRepositoryException(context(repository, source), "failed to fetch changeset");
    }
  }

  private ContextEntry.ContextBuilder context(Repository repository, String source) {
    return entity(Changeset.class, source).in(repository);
  }

}
