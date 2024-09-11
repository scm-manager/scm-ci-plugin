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
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.RepositoryContextResolver;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryLocationResolver;
import sonia.scm.repository.RepositoryManager;

import java.nio.file.Path;

public class CIStatusRepositoryContextResolver implements RepositoryContextResolver {

  private RepositoryManager repositoryManager;
  private RepositoryLocationResolver locationResolver;

  @Inject
  public CIStatusRepositoryContextResolver(RepositoryManager repositoryManager, RepositoryLocationResolver locationResolver) {
    this.repositoryManager = repositoryManager;
    this.locationResolver = locationResolver;
  }

  @Override
  public RepositoryContext resolve(String[] args) {
    NamespaceAndName namespaceAndName = extractNamespaceAndName(args);
    Repository repository = repositoryManager.get(namespaceAndName);
    Path path = locationResolver.forClass(Path.class).getLocation(repository.getId()).resolve("data");
    return new RepositoryContext(repository, path);
  }

  private NamespaceAndName extractNamespaceAndName(String[] args) {
    String ns = null;
    String name = null;
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--namespace")) {
        ns = args[i + 1];
      }
      if (args[i].equals("--name")) {
        name = args[i + 1];
      }
    }
    if (ns != null && name != null) {
      return new NamespaceAndName(ns, name);
    }
    throw new IllegalArgumentException("missing repository namespace or repository name in scm ci-update command");
  }
}
