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
import sonia.scm.migration.RepositoryUpdateContext;
import sonia.scm.migration.RepositoryUpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryLocationResolver;
import sonia.scm.update.RepositoryPermissionUpdater;
import sonia.scm.update.UpdateStepRepositoryMetadataAccess;
import sonia.scm.version.Version;

import java.nio.file.Path;

@Extension
public class RepositoryReadCIStatusPermissionUpdateStep implements RepositoryUpdateStep {

  private final RepositoryLocationResolver locationResolver;
  private final UpdateStepRepositoryMetadataAccess<Path> updateStepRepositoryMetadataAccess;
  private final RepositoryPermissionUpdater permissionUpdater;

  @Inject
  public RepositoryReadCIStatusPermissionUpdateStep(RepositoryLocationResolver locationResolver,
                                                    UpdateStepRepositoryMetadataAccess<Path> updateStepRepositoryMetadataAccess,
                                                    RepositoryPermissionUpdater permissionUpdater) {
    this.locationResolver = locationResolver;
    this.updateStepRepositoryMetadataAccess = updateStepRepositoryMetadataAccess;
    this.permissionUpdater = permissionUpdater;
  }

  @Override
  public void doUpdate(RepositoryUpdateContext context) {
    Path repositoryLocation = locationResolver.forClass(Path.class).getLocation(context.getRepositoryId());
    Repository repository = updateStepRepositoryMetadataAccess.read(repositoryLocation);
    permissionUpdater.removePermission(repository, "readCIStatus");
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
