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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.InternalRepositoryException;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SourceRevisionResolverTest {

  @Mock
  private RepositoryServiceFactory repositoryServiceFactory;

  @InjectMocks
  private SourceRevisionResolver revisionResolver;

  private final Repository repository = RepositoryTestData.createHeartOfGold();
  @Nested
  class WithBranch {

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private LogCommandBuilder logCommand;

    @BeforeEach
    void setUpRepositoryService() {
      when(repositoryServiceFactory.create(repository)).thenReturn(repositoryService);
      when(repositoryService.getLogCommand()).thenReturn(logCommand);
    }

    @Test
    void shouldReturnEmptyResultIfChangesetCouldNotBeDetermined() {
      Optional<String> sourceRevision = revisionResolver.resolveRevisionOfSource(repository, mockPullRequest("feature/spaceship", null));

      assertThat(sourceRevision).isEmpty();
    }

    @Test
    void shouldReturnChangesetIdIfBranchExists() throws IOException {
      Changeset changeset = new Changeset("42", 1L, null);
      when(logCommand.getChangeset("feature/spaceship")).thenReturn(changeset);

      Optional<String> sourceRevision = revisionResolver.resolveRevisionOfSource(repository, mockPullRequest("feature/spaceship", null));
      assertThat(sourceRevision).get().isEqualTo("42");
    }

    @Test
    void shouldThrowInternalRepositoryExceptionOnFailure() throws IOException {
      when(logCommand.getChangeset(anyString())).thenThrow(new IOException("failure"));
      PullRequest pullRequest = mockPullRequest("feature/spaceship", null);

      assertThrows(InternalRepositoryException.class, () -> revisionResolver.resolveRevisionOfSource(repository, pullRequest));
    }
  }

  @Test
  void shouldReturnChangesetIdOfPullRequestIfExists() {
    Optional<String> sourceRevision = revisionResolver.resolveRevisionOfSource(repository, mockPullRequest("feature/spaceship", "42"));
    assertThat(sourceRevision).get().isEqualTo("42");
  }

  private PullRequest mockPullRequest(String sourceBranch, String sourceRevision) {
    PullRequest pullRequest = new PullRequest("1", sourceBranch, null);
    pullRequest.setSourceRevision(sourceRevision);
    return pullRequest;
  }
}
