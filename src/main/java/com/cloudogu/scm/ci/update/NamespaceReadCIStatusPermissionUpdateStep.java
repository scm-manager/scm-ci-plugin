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

package com.cloudogu.scm.ci.update;

import jakarta.inject.Inject;
import sonia.scm.migration.NamespaceUpdateContext;
import sonia.scm.migration.NamespaceUpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Namespace;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.update.RepositoryPermissionUpdater;
import sonia.scm.version.Version;

@Extension
public class NamespaceReadCIStatusPermissionUpdateStep implements NamespaceUpdateStep {

  private final DataStoreFactory dataStoreFactory;
  private final RepositoryPermissionUpdater permissionUpdater;

  @Inject
  public NamespaceReadCIStatusPermissionUpdateStep(DataStoreFactory dataStoreFactory,
                                                   RepositoryPermissionUpdater permissionUpdater) {
    this.dataStoreFactory = dataStoreFactory;
    this.permissionUpdater = permissionUpdater;
  }

  @Override
  public void doUpdate(NamespaceUpdateContext namespaceUpdateContext) {
    DataStore<Namespace> namespaceStore = dataStoreFactory
      .withType(Namespace.class)
      .withName("namespaces")
      .build();
    namespaceStore.getOptional(namespaceUpdateContext.getNamespace())
      .ifPresent(namespace -> permissionUpdater.removePermission(namespace, "readCIStatus"));
  }

  @Override
  public Version getTargetVersion() {
    return Version.parse("3.1.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.repository.readCIStatus";
  }
}
