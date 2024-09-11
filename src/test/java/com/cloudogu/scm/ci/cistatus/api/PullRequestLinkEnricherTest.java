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

import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.pullrequest.service.PullRequestStatus;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.repository.Repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PullRequestLinkEnricherTest {

  private static final Repository REPOSITORY = new Repository("1", "git", "space", "x");

  @Mock
  private CIStatusPathBuilder pathBuilder;

  @InjectMocks
  private PullRequestLinkEnricher pullRequestLinkEnricher;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  HalEnricherContext context;
  @Mock
  HalAppender appender;

  @Mock
  Subject subject;

  PullRequest pr = new PullRequest("1", "source", "target");

  @BeforeEach
  void bindSubject() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDownSubject() {
    ThreadContext.unbindSubject();
  }

  @BeforeEach
  void mockContext() {
    when(context.oneRequireByType(Repository.class)).thenReturn(REPOSITORY);
    when(context.oneRequireByType(PullRequest.class)).thenReturn(pr);
  }

  @Test
  void shouldEnrichCILinkToPullRequestIfOpen() {
    pr.setStatus(PullRequestStatus.OPEN);

    when(subject.isPermitted("repository:readCIStatus:1")).thenReturn(true);
    when(pathBuilder.createPullRequestCiStatusCollectionUri(REPOSITORY.getNamespace(), REPOSITORY.getName(), pr.getId())).thenReturn("http://scm.com/pullRequest/" + pr.getId());

    pullRequestLinkEnricher.enrich(context, appender);

    String expectedHref = "http://scm.com/pullRequest/1";
    verify(appender).appendLink("ciStatus", expectedHref);
  }

  @Test
  void shouldNoEnrichCILinkToPullRequestIfClosed() {
    pr.setStatus(PullRequestStatus.REJECTED);

    pullRequestLinkEnricher.enrich(context, appender);

    verify(appender, never()).appendLink(any(), any());
  }

  @Test
  void shouldNotEnrichCiLinkIfNotPermitted() {
    pr.setStatus(PullRequestStatus.OPEN);

    pullRequestLinkEnricher.enrich(context, appender);

    verify(appender, never()).appendLink(any(), any());
  }
}
