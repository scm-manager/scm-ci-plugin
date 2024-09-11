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

import sonia.scm.protocolcommand.CommandInterpreter;
import sonia.scm.protocolcommand.RepositoryContextResolver;
import sonia.scm.protocolcommand.ScmCommandProtocol;

public class CIStatusCommandInterpreter implements CommandInterpreter {
  private RepositoryContextResolver repositoryContextResolver;
  private ScmCommandProtocol protocolHandler;
  private String[] args;

  public CIStatusCommandInterpreter(RepositoryContextResolver repositoryContextResolver, ScmCommandProtocol protocolHandler, String[] args) {
    this.repositoryContextResolver = repositoryContextResolver;
    this.protocolHandler = protocolHandler;
    this.args = args;
  }

  @Override
  public String[] getParsedArgs() {
    return args;
  }

  @Override
  public ScmCommandProtocol getProtocolHandler() {
    return protocolHandler;
  }

  @Override
  public RepositoryContextResolver getRepositoryContextResolver() {
    return repositoryContextResolver;
  }
}
