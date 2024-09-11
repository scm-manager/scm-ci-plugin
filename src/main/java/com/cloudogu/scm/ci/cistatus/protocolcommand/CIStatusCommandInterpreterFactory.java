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

import jakarta.inject.Inject;
import sonia.scm.plugin.Extension;
import sonia.scm.protocolcommand.CommandInterpreter;
import sonia.scm.protocolcommand.CommandInterpreterFactory;

import java.util.Optional;

import static java.util.Optional.of;

@Extension
public class CIStatusCommandInterpreterFactory implements CommandInterpreterFactory {
  private final CIStatusRepositoryContextResolver repositoryContextResolver;
  private final CIStatusCommandProtocol protocolHandler;

  @Inject
  public CIStatusCommandInterpreterFactory(CIStatusRepositoryContextResolver repositoryContextResolver, CIStatusCommandProtocol protocolHandler) {
    this.repositoryContextResolver = repositoryContextResolver;
    this.protocolHandler = protocolHandler;
  }

  @Override
  public Optional<CommandInterpreter> canHandle(String command) {
    if (command.startsWith("scm-ci-update")) {
      return of(new CIStatusCommandInterpreter(repositoryContextResolver, protocolHandler, CIStatusCommandParser.parse(command)));
    }
    return Optional.empty();
  }
}
