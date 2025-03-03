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
import sonia.scm.migration.RepositoryUpdateContext;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryLocationResolver;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.update.RepositoryPermissionUpdater;
import sonia.scm.update.UpdateStepRepositoryMetadataAccess;

import java.nio.file.Path;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryReadCIStatusPermissionUpdateStepTest {

  private final Repository repository = RepositoryTestData.createHeartOfGold();

  @Mock
  private RepositoryLocationResolver locationResolver;
  @Mock
  private RepositoryLocationResolver.RepositoryLocationResolverInstance<Path> resolverInstance;
  @Mock
  private RepositoryPermissionUpdater permissionUpdater;

  private RepositoryReadCIStatusPermissionUpdateStep step;

  @BeforeEach
  void setup() {
    UpdateStepRepositoryMetadataAccess<Path> metadataAccess = location -> repository;
    step = new RepositoryReadCIStatusPermissionUpdateStep(locationResolver, metadataAccess, permissionUpdater);
  }

  @Test
  void shouldOnlyRemoveReadCIStatusPermission() {
    when(locationResolver.forClass(Path.class)).thenReturn(resolverInstance);
    when(resolverInstance.getLocation(repository.getId())).thenReturn(Path.of("/some/path"));

    step.doUpdate(new RepositoryUpdateContext(repository.getId()));

    verify(permissionUpdater).removePermission(repository, "readCIStatus");
  }
}
