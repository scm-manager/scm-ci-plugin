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
