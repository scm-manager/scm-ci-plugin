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
    return null;
  }
}
