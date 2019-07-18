package com.cloudogu.scm.ci;

import sonia.scm.ContextEntry;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;

import javax.inject.Inject;

import static sonia.scm.NotFoundException.notFound;

public class RepositoryResolver {

  private final RepositoryManager manager;

  @Inject
  public RepositoryResolver(RepositoryManager manager) {
    this.manager = manager;
  }

  public Repository resolve(String namespace, String name) {
    return resolve(new NamespaceAndName(namespace, name));
  }

  public Repository resolve(NamespaceAndName namespaceAndName) {
    Repository repository = manager.get(namespaceAndName);
    if (repository == null) {
      throw notFound(ContextEntry.ContextBuilder.entity(namespaceAndName));
    }

    return repository;
  }
}
