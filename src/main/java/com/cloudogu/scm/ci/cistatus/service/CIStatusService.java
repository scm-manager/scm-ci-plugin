package com.cloudogu.scm.ci.cistatus.service;

import com.google.inject.Inject;
import sonia.scm.repository.Repository;

import java.util.Collection;

public class CIStatusService {

    private CIStatusStoreFactory storeFactory;

    @Inject
    public CIStatusService(CIStatusStoreFactory storeFactory) {
        this.storeFactory = storeFactory;
    }

    public void add(Repository repository, String changeSetId, CIStatus ciStatus) {
       getStore(repository, changeSetId).store(ciStatus.getType(), ciStatus);
    }

    public Collection<CIStatus> get(Repository repository, String changeSetId, String type) {
        return getStore(repository, changeSetId).getByType(type);
    }

    public Collection<CIStatus> getAll(Repository repository, String changeSetId) {
        return getStore(repository, changeSetId).getAll().values();
    }

    private CIStatusStore getStore(Repository repository, String changeSetId) {
        return storeFactory.create(repository, changeSetId);
    }
}
