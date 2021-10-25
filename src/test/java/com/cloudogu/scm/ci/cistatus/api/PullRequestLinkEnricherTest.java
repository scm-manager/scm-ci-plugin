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
    PullRequest pr = new PullRequest();
    pr.setStatus(PullRequestStatus.REJECTED);

    pullRequestLinkEnricher.enrich(context, appender);

    verify(appender, never()).appendLink(any(), any());
  }

  @Test
  void shouldNotEnrichCiLinkIfNotPermitted() {
    PullRequest pr = new PullRequest();

    pullRequestLinkEnricher.enrich(context, appender);

    verify(appender, never()).appendLink(any(), any());
  }
}
