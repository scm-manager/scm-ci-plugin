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

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import jakarta.inject.Inject;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;

import java.util.stream.Collectors;

import static com.cloudogu.scm.ci.PermissionCheck.mayRead;

@Extension
@Enrich(Changeset.class)
public class ChangesetStatusEnricher implements HalEnricher {

  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;
  private final CIStatusPathBuilder pathBuilder;

  @Inject
  public ChangesetStatusEnricher(CIStatusService ciStatusService, CIStatusMapper mapper, CIStatusPathBuilder pathBuilder) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.pathBuilder = pathBuilder;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    Changeset changeset = context.oneRequireByType(Changeset.class);

    if (mayRead(repository)) {
      appender.appendLink("ciStatus", pathBuilder.createChangesetCiStatusCollectionUri(repository.getNamespace(), repository.getName(), changeset.getId()));
      appender.appendEmbedded("ciStatus",
        ciStatusService.get(CIStatusStore.CHANGESET_STORE, repository, changeset.getId())
          .stream()
          .map(ciStatus -> mapper.map(repository, changeset.getId(), ciStatus))
          .collect(Collectors.toList()));
    }
  }
}

