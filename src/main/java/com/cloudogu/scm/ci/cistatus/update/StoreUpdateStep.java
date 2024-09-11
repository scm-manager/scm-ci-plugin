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

package com.cloudogu.scm.ci.cistatus.update;

import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import jakarta.inject.Inject;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.RepositoryLocationResolver;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.version.Version;

import java.nio.file.Path;

import static com.cloudogu.scm.ci.cistatus.CIStatusStore.CHANGESET_STORE;
import static sonia.scm.version.Version.parse;

@Extension
public class StoreUpdateStep implements UpdateStep {

  private final RepositoryLocationResolver repositoryLocationResolver;
  private final DataStoreFactory dataStoreFactory;

  @Inject
  public StoreUpdateStep(RepositoryLocationResolver repositoryLocationResolver, DataStoreFactory dataStoreFactory) {
    this.repositoryLocationResolver = repositoryLocationResolver;
    this.dataStoreFactory = dataStoreFactory;
  }

  @Override
  public void doUpdate() {
    repositoryLocationResolver.forClass(Path.class).forAllLocations((repositoryId, path) -> {
      DataStore<CIStatusCollection> newStore = dataStoreFactory
        .withType(CIStatusCollection.class)
        .withName(CHANGESET_STORE.name)
        .forRepository(repositoryId)
        .build();
      dataStoreFactory
        .withType(CIStatusCollection.class)
        .withName("ciStatus")
        .forRepository(repositoryId)
        .build()
        .getAll()
        .forEach(newStore::put);
    });
  }

  @Override
  public Version getTargetVersion() {
    return parse("2.0.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.ci.ciStatus.xml";
  }

}
