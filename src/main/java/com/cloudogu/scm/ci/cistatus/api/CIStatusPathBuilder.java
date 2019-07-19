package com.cloudogu.scm.ci.cistatus.api;

import com.google.inject.Inject;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import javax.inject.Provider;

public class CIStatusPathBuilder {

  private final Provider<ScmPathInfoStore> pathInfoStore;

  @Inject
  public CIStatusPathBuilder(Provider<ScmPathInfoStore> pathInfoStore) {
    this.pathInfoStore = pathInfoStore;
  }

  String createCiStatusSelfUri(String namespace, String name, String changesetId, String type, String ciName) {
    LinkBuilder linkBuilder = new LinkBuilder(pathInfoStore.get().get(), CIStatusRootResource.class, CIStatusResource.class);
    return linkBuilder
      .method("getCIStatusResource").parameters(namespace, name, changesetId)
      .method("get").parameters(type, ciName)
      .href();
  }
}
