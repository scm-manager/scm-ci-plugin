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
package com.cloudogu.scm.ci.cistatus.service;

import com.cloudogu.scm.ci.PermissionCheck;
import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.InternalRepositoryException;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import static sonia.scm.ContextEntry.ContextBuilder.entity;
import static sonia.scm.NotFoundException.notFound;

@Singleton
public class CIStatusService {

  private final DataStoreFactory dataStoreFactory;
  private final RepositoryServiceFactory repositoryServiceFactory;

  @Inject
  public CIStatusService(DataStoreFactory dataStoreFactory, RepositoryServiceFactory repositoryServiceFactory) {
    this.dataStoreFactory = dataStoreFactory;
    this.repositoryServiceFactory = repositoryServiceFactory;
  }

  public void put(CIStatusStore store, Repository repository, String id, CIStatus ciStatus) {
    PermissionCheck.checkWrite(repository);
    CIStatusCollection ciStatusCollection = get(store, repository, id);
    ciStatusCollection.put(ciStatus);
    getStore(store, repository).put(id, ciStatusCollection);
  }

  public CIStatusCollection get(CIStatusStore storeName, Repository repository, String id) {
    PermissionCheck.checkRead(repository);
    CIStatusCollection collection = getStore(storeName, repository).get(id);
    return  collection != null ? collection : new CIStatusCollection();
  }

  public CIStatusCollection getByBranch(CIStatusStore storeName, Repository repository, String branch) {
    try (RepositoryService service = repositoryServiceFactory.create(repository)) {
      final ChangesetPagingResult changesets = service.getLogCommand().setBranch(branch).setPagingLimit(1).getChangesets();
      if (changesets.getChangesets().isEmpty()) {
        throw notFound(entity("Branch", branch).in(repository));
      }
      return get(storeName, repository, changesets.getChangesets().get(0).getId());
    } catch (IOException e) {
      throw new InternalRepositoryException(
        entity("Branch", branch).in(repository).build(),
        "could not read changeset for branch " + branch + " in repository " + repository,
        e);
    }
  }

  private DataStore<CIStatusCollection> getStore(CIStatusStore store, Repository repository) {
    return dataStoreFactory.withType(CIStatusCollection.class).withName(store.name).forRepository(repository).build();
  }
}
