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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.migration.NamespaceUpdateContext;
import sonia.scm.repository.Namespace;
import sonia.scm.store.DataStore;
import sonia.scm.store.InMemoryByteDataStoreFactory;
import sonia.scm.update.RepositoryPermissionUpdater;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NamespaceReadCIStatusPermissionUpdateStepTest {

  private final InMemoryByteDataStoreFactory dataStoreFactory = new InMemoryByteDataStoreFactory();
  @Mock
  private RepositoryPermissionUpdater permissionUpdater;
  private NamespaceReadCIStatusPermissionUpdateStep step;

  @BeforeEach
  void setup() {
    step = new NamespaceReadCIStatusPermissionUpdateStep(dataStoreFactory, permissionUpdater);
  }

  @Test
  void shouldOnlyRemoveReadCIStatusPermission() {
    DataStore<Namespace> namespaceStore = dataStoreFactory
      .withType(Namespace.class)
      .withName("namespaces")
      .build();
    Namespace namespace = new Namespace("Kanto");
    namespaceStore.put(namespace.getId(), namespace);

    step.doUpdate(new NamespaceUpdateContext(namespace.getId()));

    verify(permissionUpdater).removePermission(namespace, "readCIStatus");
  }
}
