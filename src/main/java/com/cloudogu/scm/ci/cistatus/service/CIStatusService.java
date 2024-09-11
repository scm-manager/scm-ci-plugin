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

package com.cloudogu.scm.ci.cistatus.service;

import com.cloudogu.scm.ci.PermissionCheck;
import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import sonia.scm.event.ScmEventBus;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.InternalRepositoryException;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import java.io.IOException;

import static com.cloudogu.scm.ci.cistatus.CIStatusStore.CHANGESET_STORE;
import static sonia.scm.ContextEntry.ContextBuilder.entity;
import static sonia.scm.NotFoundException.notFound;

@Singleton
public class CIStatusService {

  private final DataStoreFactory dataStoreFactory;
  private final RepositoryServiceFactory repositoryServiceFactory;
  private final ScmEventBus eventBus;

  @Inject
  public CIStatusService(DataStoreFactory dataStoreFactory, RepositoryServiceFactory repositoryServiceFactory, ScmEventBus eventBus) {
    this.dataStoreFactory = dataStoreFactory;
    this.repositoryServiceFactory = repositoryServiceFactory;
    this.eventBus = eventBus;
  }

  public void put(CIStatusStore store, Repository repository, String id, CIStatus ciStatus) {
    PermissionCheck.checkWrite(repository);
    CIStatusCollection ciStatusCollection = get(store, repository, id);
    ciStatusCollection.put(ciStatus);
    getStore(store, repository).put(id, ciStatusCollection);
    eventBus.post(new CIStatusEvent(repository, store, id, ciStatus));
  }

  public CIStatusCollection get(CIStatusStore storeName, Repository repository, String id) {
    PermissionCheck.checkRead(repository);
    CIStatusCollection collection = getStore(storeName, repository).get(id);
    return  collection != null ? collection : new CIStatusCollection();
  }

  public CIStatusCollection getByBranch(Repository repository, String branch) {
    try (RepositoryService service = repositoryServiceFactory.create(repository)) {
      final ChangesetPagingResult changesets = service.getLogCommand().setBranch(branch).setPagingLimit(1).getChangesets();
      if (changesets.getChangesets().isEmpty()) {
        throw notFound(entity("Branch", branch).in(repository));
      }
      return get(CHANGESET_STORE, repository, changesets.getChangesets().get(0).getId());
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
