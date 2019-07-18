package com.cloudogu.scm.ci.cistatus.service;

import com.google.inject.Inject;
import sonia.scm.store.DataStore;

import java.util.Collection;
import java.util.Map;


public class CIStatusStore {

    private final DataStore<CIStatus> store;

    @Inject
    CIStatusStore(DataStore<CIStatus> store) {
        this.store = store;
    }

    public Collection<CIStatus> get(String type, String name) {
        return null;
    }

    public Map<String, CIStatus> getAll() {
        return store.getAll();
    }

    public void store(String type, CIStatus ciStatus) {
        store.put(type, ciStatus);
    }
}
