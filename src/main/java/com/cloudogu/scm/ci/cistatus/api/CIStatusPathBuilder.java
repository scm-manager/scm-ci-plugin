/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.cloudogu.scm.ci.cistatus.api;

import com.google.inject.Inject;
import jakarta.inject.Provider;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

class CIStatusPathBuilder {

  private final Provider<ScmPathInfoStore> pathInfoStore;

  @Inject
  public CIStatusPathBuilder(Provider<ScmPathInfoStore> pathInfoStore) {
    this.pathInfoStore = pathInfoStore;
  }

  String createChangesetCiStatusSelfUri(String namespace, String name, String changesetId, String type, String ciName) {
    LinkBuilder linkBuilder = new LinkBuilder(pathInfoStore.get().get(), ChangesetCIStatusRootResource.class, ChangesetCIStatusResource.class);
    return linkBuilder
      .method("getChangesetCIStatusResource").parameters(namespace, name, changesetId)
      .method("get").parameters(type, ciName)
      .href();
  }

  String createChangesetCiStatusCollectionUri(String namespace, String name, String changesetId) {
    LinkBuilder linkBuilder = new LinkBuilder(pathInfoStore.get().get(), ChangesetCIStatusRootResource.class, ChangesetCIStatusResource.class);
    return linkBuilder
      .method("getChangesetCIStatusResource").parameters(namespace, name, changesetId)
      .method("getAll").parameters()
      .href();
  }

  String createPullRequestCiStatusCollectionUri(String namespace, String name, String pullRequestId) {
    LinkBuilder linkBuilder = new LinkBuilder(pathInfoStore.get().get(), PullRequestCIStatusRootResource.class, PullRequestCIStatusResource.class);
    return linkBuilder
      .method("getPullRequestCIStatusResource").parameters(namespace, name, pullRequestId)
      .method("getAll").parameters()
      .href();
  }
}
