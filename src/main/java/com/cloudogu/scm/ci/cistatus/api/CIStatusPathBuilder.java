package com.cloudogu.scm.ci.cistatus.api;

import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import javax.inject.Inject;
import javax.inject.Provider;

public class CIStatusPathBuilder {

    private final Provider<ScmPathInfoStore> pathInfoStore;

    @Inject
    CIStatusPathBuilder(Provider<ScmPathInfoStore> pathInfoStore) {
        this.pathInfoStore = pathInfoStore;
    }

    String createCIStatusSelfUri(String namespace, String name, String pullRequestId) {
        LinkBuilder linkBuilder = new LinkBuilder(pathInfoStore.get().get(), CIStatusRootResource.class, CIStatusResource.class);
        return linkBuilder
                .method("getCIStatusResource").parameters(namespace, name, pullRequestId)
                .href();
    }
}
