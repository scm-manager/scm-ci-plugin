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
