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

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.ci.cistatus.service.Status;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.workflow.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CIStatusResolverTest {

  @Mock
  private CIStatusService ciStatusService;

  @Mock
  private SourceRevisionResolver sourceRevisionResolver;

  @InjectMocks
  private CIStatusResolver resolver;

  @Mock
  private Context context;

  private final Repository repository = RepositoryTestData.createHeartOfGold();
  private final PullRequest pullRequest = createPullRequest();

  @BeforeEach
  void mockContext() {
    when(context.getRepository()).thenReturn(repository);
    when(context.getPullRequest()).thenReturn(pullRequest);
  }

  @Test
  void shouldResolveCiStatus() {
    CIStatus changesetCiStatus = new CIStatus("jenkins", "jenkins", "jenkins", Status.SUCCESS, "jenkins.io");
    CIStatusCollection changesetCiStatuses = new CIStatusCollection();
    changesetCiStatuses.put(changesetCiStatus);

    CIStatus prCiStatus = new CIStatus("teamscale", "teamscale", "Teamscale", Status.SUCCESS, "teamscale.com");
    CIStatusCollection pullRequestCiStatuses = new CIStatusCollection();
    pullRequestCiStatuses.put(prCiStatus);

    when(sourceRevisionResolver.resolveRevisionOfSource(repository, pullRequest)).thenReturn(of("42"));
    when(ciStatusService.get(CIStatusStore.CHANGESET_STORE, repository, "42")).thenReturn(changesetCiStatuses);
    when(ciStatusService.get(CIStatusStore.PULL_REQUEST_STORE, repository, "21")).thenReturn(pullRequestCiStatuses);

    CIStatusCollection resolved = resolver.resolve(context, false);
    assertThat(resolved).containsExactly(changesetCiStatus, prCiStatus);
  }

  @Test
  void shouldIgnoreMissingRevision() {
    when(sourceRevisionResolver.resolveRevisionOfSource(repository, pullRequest)).thenReturn(empty());
    when(ciStatusService.get(CIStatusStore.PULL_REQUEST_STORE, repository, "21")).thenReturn(new CIStatusCollection());

    CIStatusCollection resolved = resolver.resolve(context, false);
    assertThat(resolved).isEmpty();

    verify(ciStatusService, never()).get(eq(CIStatusStore.CHANGESET_STORE), any(), any());
  }

  private PullRequest createPullRequest() {
    PullRequest pullRequest = new PullRequest();
    pullRequest.setSource("feature/spaceship");
    pullRequest.setId("21");
    return pullRequest;
  }
}
