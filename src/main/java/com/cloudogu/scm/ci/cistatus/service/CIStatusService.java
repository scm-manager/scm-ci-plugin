package com.cloudogu.scm.ci.cistatus.service;

import com.cloudogu.scm.ci.PermissionCheck;
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

  public void put(Repository repository, String changesetId, CIStatusCollection collection) {
    PermissionCheck.checkWrite(repository);
    getStore(repository).put(changesetId, collection);
  }

  public CIStatusCollection get(Repository repository, String changesetId) {
    PermissionCheck.checkRead(repository);
    CIStatusCollection collection = getStore(repository).get(changesetId);
    return  collection != null ? collection : new CIStatusCollection();
  }

  private DataStore<CIStatusCollection> getStore(Repository repository) {
    return dataStoreFactory.withType(CIStatusCollection.class).withName("ciStatus").forRepository(repository).build();
  }
}
