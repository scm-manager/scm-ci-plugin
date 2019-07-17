package com.cloudogu.scm.ci.cistatus.service;

import com.google.inject.Inject;
import sonia.scm.repository.Repository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

public class CIStatusStoreFactory {

    private final DataStoreFactory dataStoreFactory;

    @Inject
    public CIStatusStoreFactory(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    public CIStatusStore create(Repository repository, String changeSetId) {
        DataStore<CIStatus> store = dataStoreFactory.withType(CIStatus.class).withName(changeSetId).forRepository(repository).build();
        return new CIStatusStore(store);
    }

    public CIStatusStore create(String repositoryId, String changeSetId) {
        DataStore<CIStatus> store = dataStoreFactory.withType(CIStatus.class).withName(changeSetId).forRepository(repositoryId).build();
        return new CIStatusStore(store);
    }
}
