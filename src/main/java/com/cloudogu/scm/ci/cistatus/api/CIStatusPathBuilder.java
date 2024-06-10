/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
