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
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.workflow.Context;
import jakarta.inject.Inject;
import sonia.scm.repository.Repository;

class CIStatusResolver {

  private final CIStatusService ciStatusService;
  private final SourceRevisionResolver sourceRevisionResolver;

  @Inject
  CIStatusResolver(CIStatusService ciStatusService, SourceRevisionResolver sourceRevisionResolver) {
    this.ciStatusService = ciStatusService;
    this.sourceRevisionResolver = sourceRevisionResolver;
  }

  CIStatusCollection resolve(Context context, boolean ignoreChangesetStatus) {
    Repository repository = context.getRepository();
    PullRequest pullRequest = context.getPullRequest();

    CIStatusCollection ciStatusCollection = new CIStatusCollection();

    if (!ignoreChangesetStatus) {
      sourceRevisionResolver.resolveRevisionOfSource(repository, pullRequest)
        .ifPresent(sourceRevision -> ciStatusService.get(CIStatusStore.CHANGESET_STORE, repository, sourceRevision)
          .stream()
          .forEach(ciStatusCollection::put));
    }

    ciStatusService.get(CIStatusStore.PULL_REQUEST_STORE, repository, pullRequest.getId())
      .stream()
      .forEach(ciStatusCollection::put);

    return ciStatusCollection;
  }

}
