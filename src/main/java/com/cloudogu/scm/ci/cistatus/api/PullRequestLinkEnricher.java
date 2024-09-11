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
import jakarta.inject.Inject;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.Requires;
import sonia.scm.repository.Repository;

import static com.cloudogu.scm.ci.PermissionCheck.mayRead;

@Requires("scm-review-plugin")
@Enrich(PullRequest.class)
@Extension
public class PullRequestLinkEnricher implements HalEnricher {

  private final CIStatusPathBuilder pathBuilder;

  @Inject
  public PullRequestLinkEnricher(CIStatusPathBuilder pathBuilder) {
    this.pathBuilder = pathBuilder;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    PullRequest pullRequest = context.oneRequireByType(PullRequest.class);

    // In the following we check for the #name of the status, because
    // - the draft status was introduced in the review plugin with version 2.26.0
    // - this version of the review plugin requires core version 2.40.0
    // - but the minimal core version of the ci plugin is 2.39.0
    // - and we don't want to increase this minimal version just for the drafts
    if (("OPEN".equals(pullRequest.getStatus().name()) || "DRAFT".equals(pullRequest.getStatus().name())) && mayRead(repository)) {
      appender.appendLink("ciStatus", pathBuilder.createPullRequestCiStatusCollectionUri(repository.getNamespace(), repository.getName(), pullRequest.getId()));
    }
  }
}
