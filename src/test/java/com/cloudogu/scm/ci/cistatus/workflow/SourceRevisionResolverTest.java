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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.NotFoundException;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
