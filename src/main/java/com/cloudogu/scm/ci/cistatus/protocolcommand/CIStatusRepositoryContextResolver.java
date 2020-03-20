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
package com.cloudogu.scm.ci.cistatus.protocolcommand;

import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.RepositoryContextResolver;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryLocationResolver;
import sonia.scm.repository.RepositoryManager;

import javax.inject.Inject;
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
