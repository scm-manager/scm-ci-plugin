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

package com.cloudogu.scm.ci.cistatus.protocolcommand;

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import jakarta.inject.Inject;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.ScmCommandProtocol;

public class CIStatusCommandProtocol implements ScmCommandProtocol {

  private final CIStatusService service;
  private final CIStatusUnmarshaller unmarshaller;

  @Inject
  public CIStatusCommandProtocol(CIStatusService service, CIStatusUnmarshaller unmarshaller) {
    this.service = service;
    this.unmarshaller = unmarshaller;
  }

  @Override
  public void handle(CommandContext context, RepositoryContext repositoryContext) {
    CIStatus ciStatus = unmarshaller.unmarshal(context.getInputStream());
    service.put(CIStatusStore.CHANGESET_STORE, repositoryContext.getRepository(), extractRevision(context), ciStatus);
  }

  private String extractRevision(CommandContext context) {
    String[] args = context.getArgs();
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--revision")) {
        return args[i + 1];
      }
    }
    throw new IllegalArgumentException("missing revision in scm ci-update command");
  }
}
