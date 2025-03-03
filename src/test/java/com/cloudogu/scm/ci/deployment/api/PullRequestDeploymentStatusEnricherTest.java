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

package com.cloudogu.scm.ci.deployment.api;

import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.pullrequest.service.PullRequestStatus;
import com.google.inject.util.Providers;
import jakarta.inject.Provider;
import org.github.sdorra.jse.ShiroExtension;
import org.github.sdorra.jse.SubjectAware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.Repository;

import java.net.URI;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, ShiroExtension.class})
@SubjectAware("Trainer Red")
class PullRequestDeploymentStatusEnricherTest {

  private final Repository repository = new Repository("1337", "git", "pokemon", "platinum");
  private final String domainForLinks = "https://scm.com/api/";
  private PullRequest pullRequest;
  private PullRequestDeploymentStatusEnricher enricher;

  @Mock
  private HalEnricherContext context;
  @Mock
  private HalAppender appender;

  @BeforeEach
  void setup() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create(domainForLinks));
    Provider<ScmPathInfoStore> scmPathInfoStoreProvider = Providers.of(scmPathInfoStore);
    DeploymentStatusPathBuilder pathBuilder = new DeploymentStatusPathBuilder(scmPathInfoStoreProvider);

    pullRequest = new PullRequest("2", "source", "target");
    enricher = new PullRequestDeploymentStatusEnricher(pathBuilder);
  }

  @Test
  void shouldNotEnrichBecauseOfMissingPermission() {
    when(context.oneRequireByType(Repository.class)).thenReturn(repository);
    enricher.enrich(context, appender);
    verifyNoInteractions(appender);
  }

  @Test
  @SubjectAware(permissions = "repository:read:1337")
  void shouldEnrichDeploymentStatusLinkIfPullRequestIsOpen() {
    when(context.oneRequireByType(Repository.class)).thenReturn(repository);
    when(context.oneRequireByType(PullRequest.class)).thenReturn(pullRequest);
    pullRequest.setStatus(PullRequestStatus.OPEN);

    enricher.enrich(context, appender);

    verify(appender).appendLink(
      "deploymentStatus",
      "https://scm.com/api/v2/deployments/pokemon/platinum/pull-requests/2"
    );
  }

  @Test
  @SubjectAware(permissions = "repository:read:1337")
  void shouldEnrichDeploymentStatusLinkIfPullRequestIsDraft() {
    when(context.oneRequireByType(Repository.class)).thenReturn(repository);
    when(context.oneRequireByType(PullRequest.class)).thenReturn(pullRequest);
    pullRequest.setStatus(PullRequestStatus.DRAFT);

    enricher.enrich(context, appender);

    verify(appender).appendLink(
      "deploymentStatus",
      "https://scm.com/api/v2/deployments/pokemon/platinum/pull-requests/2"
    );
  }

  @Test
  @SubjectAware(permissions = "repository:read:1337")
  void shouldNotEnrichDeploymentStatusLinkIfPullRequestIsRejected() {
    when(context.oneRequireByType(Repository.class)).thenReturn(repository);
    when(context.oneRequireByType(PullRequest.class)).thenReturn(pullRequest);
    pullRequest.setStatus(PullRequestStatus.REJECTED);

    enricher.enrich(context, appender);

    verifyNoInteractions(appender);
  }

  @Test
  @SubjectAware(permissions = "repository:read:1337")
  void shouldNotEnrichDeploymentStatusLinkIfPullRequestIsMerged() {
    when(context.oneRequireByType(Repository.class)).thenReturn(repository);
    when(context.oneRequireByType(PullRequest.class)).thenReturn(pullRequest);
    pullRequest.setStatus(PullRequestStatus.MERGED);

    enricher.enrich(context, appender);

    verifyNoInteractions(appender);
  }
}
