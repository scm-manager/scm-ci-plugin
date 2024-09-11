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
