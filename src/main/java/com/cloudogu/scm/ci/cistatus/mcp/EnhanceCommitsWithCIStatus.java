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

package com.cloudogu.scm.ci.cistatus.mcp;

import com.cloudogu.mcp.ToolListCommits;
import com.cloudogu.mcp.ToolListCommitsFilterEnhancement;
import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import jakarta.inject.Inject;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.Requires;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.cloudogu.scm.ci.PermissionCheck.mayRead;

@Extension
@Requires("scm-mcp-plugin")
class EnhanceCommitsWithCIStatus implements ToolListCommitsFilterEnhancement {

  private final CIStatusService ciStatusService;

  @Inject
  public EnhanceCommitsWithCIStatus(CIStatusService ciStatusService) {
    this.ciStatusService = ciStatusService;
  }

  @Override
  public Optional<Class<?>> getInputClass() {
    return Optional.empty();
  }

  @Override
  public String getNamespace() {
    return "ci";
  }

  @Override
  public void enhanceStructuredResult(Repository repository, Changeset changeset, ToolListCommits.CompositeInput input, BiConsumer<String, Object> keyValueConsumer) {
    if (mayRead(repository)) {
      keyValueConsumer.accept(
        "ciStatus",
        ciStatusService.get(CIStatusStore.CHANGESET_STORE, repository, changeset.getId()).stream().collect(Collectors.toList())
      );
    }
  }
}
