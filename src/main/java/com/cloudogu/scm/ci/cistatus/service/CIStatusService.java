package com.cloudogu.scm.ci.cistatus.service;

import sonia.scm.repository.Repository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CIStatusService {

  private final DataStoreFactory dataStoreFactory;

  @Inject
  public CIStatusService(DataStoreFactory dataStoreFactory) {
    this.dataStoreFactory = dataStoreFactory;
  }

  public void put(Repository repository, String changeset, CIStatusCollection collection) {
    getStore(repository).put(changeset, collection);
  }

  public CIStatusCollection get(Repository repository, String changeset) {
    CIStatusCollection collection = getStore(repository).get(changeset);
    return  collection != null ? collection : new CIStatusCollection();
  }

  private DataStore<CIStatusCollection> getStore(Repository repository) {
    return dataStoreFactory.withType(CIStatusCollection.class).withName("ciStatus").forRepository(repository).build();
  }
}
