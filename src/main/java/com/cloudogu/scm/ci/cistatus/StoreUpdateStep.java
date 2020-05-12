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

package com.cloudogu.scm.ci.cistatus;

import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.AbstractRepositoryManager;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryLocationResolver;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.version.Version;

import javax.inject.Inject;

import java.nio.file.Path;
import java.util.Collection;

import static sonia.scm.version.Version.parse;

@Extension
public class StoreUpdateStep implements UpdateStep {

  private final DataStoreFactory storeFactory;
  private final RepositoryLocationResolver repositoryLocationResolver;
  private final RepositoryManager repositoryManager;

  @Inject
  public StoreUpdateStep(DataStoreFactory storeFactory, RepositoryLocationResolver repositoryLocationResolver, RepositoryManager repositoryManager) {
    this.storeFactory = storeFactory;
    this.repositoryLocationResolver = repositoryLocationResolver;
    this.repositoryManager = repositoryManager;
  }

  @Override
  public void doUpdate() {
    repositoryLocationResolver.forClass(Path.class).forAllLocations((repositoryId, path) -> {
      Repository repository = repositoryManager.get(repositoryId);

      DataStore<CIStatusCollection> oldStore = getStore("ciStatus", repository);
      Collection<CIStatusCollection> oldStoreItems = oldStore.getAll().values();
      DataStore<CIStatusCollection> newStore = getStore("changesetCIStatus", repository);

      oldStoreItems.forEach(newStore::put);
    });
  }

  @Override
  public Version getTargetVersion() {
    return parse("2.0.0");
  }

  @Override
  public String getAffectedDataType() {
    return "com.cloudogu.scm.ci.ciStatus.xml";
  }

  private DataStore<CIStatusCollection> getStore(String storename, Repository repository) {
    return storeFactory.withType(CIStatusCollection.class).withName(storename).forRepository(repository).build();
  }
}
