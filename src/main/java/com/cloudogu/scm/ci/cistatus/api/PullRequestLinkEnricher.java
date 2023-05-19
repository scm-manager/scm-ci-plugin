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
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.Requires;
import sonia.scm.repository.Repository;

import javax.inject.Inject;

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
