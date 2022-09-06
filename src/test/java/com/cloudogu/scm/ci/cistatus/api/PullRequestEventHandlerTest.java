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

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.ci.cistatus.service.Status;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.pullrequest.service.PullRequestMergedEvent;
import com.cloudogu.scm.review.pullrequest.service.PullRequestRejectedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PullRequestEventHandlerTest {

  private final Repository repository = RepositoryTestData.create42Puzzle();

  private PullRequest pr;
  @Mock
  private CIStatusService service;

  @InjectMocks
  private PullRequestEventHandler eventHandler;

  @BeforeEach
  void mockStore() {
    repository.setId("42");
    pr = new PullRequest("42", "src", "trgt");
    CIStatusCollection collection = CIStatusCollection.toCollection(List.of(
      new CIStatus("1", "first", "First", Status.FAILURE, ""),
      new CIStatus("2nd", "second", "Second", Status.PENDING, ""),
      new CIStatus("third", "III", "Spanish Inquisition", Status.SUCCESS, "")
    ));
    when(service.get(any(), any(), any())).thenReturn(collection);
  }

  @Test
  void shouldDeleteAllUnfinishedCiStatusOnPRMerge() {
    eventHandler.handleMergedPRs(new PullRequestMergedEvent(repository, pr));

    verify(service).overwriteCollection(eq(CIStatusStore.PULL_REQUEST_STORE), eq(repository), eq("42"), argThat(argument -> {
      assertThat(argument).noneMatch(status -> status.getStatus().equals(Status.PENDING));
      assertThat(argument).hasSize(2);
      return true;
    }));
  }

  @Test
  void shouldDeleteAllUnfinishedCiStatusOnPRReject() {
    eventHandler.handleRejectedPRs(new PullRequestRejectedEvent(repository, pr, PullRequestRejectedEvent.RejectionCause.BRANCH_DELETED));

    service.get(CIStatusStore.PULL_REQUEST_STORE, repository, pr.getId());

    verify(service).overwriteCollection(eq(CIStatusStore.PULL_REQUEST_STORE), eq(repository), eq("42"), argThat(argument -> {
      assertThat(argument).noneMatch(status -> status.getStatus().equals(Status.PENDING));
      assertThat(argument).hasSize(2);
      return true;
    }));
  }
}
