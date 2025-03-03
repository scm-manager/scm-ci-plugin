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
import lombok.extern.slf4j.Slf4j;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.security.AssignedPermission;
import sonia.scm.store.ConfigurationEntryStore;
import sonia.scm.store.ConfigurationEntryStoreFactory;
import sonia.scm.version.Version;

import java.util.List;
import java.util.Map;

@Extension
@Slf4j
public class ReadCIStatusPermissionUpdateStep implements UpdateStep {

  private final ConfigurationEntryStoreFactory entryStoreFactory;

  @Inject
  public ReadCIStatusPermissionUpdateStep(ConfigurationEntryStoreFactory entryStoreFactory) {
    this.entryStoreFactory = entryStoreFactory;
  }

  @Override
  public void doUpdate() {
    ConfigurationEntryStore<AssignedPermission> securityStore = entryStoreFactory
      .withType(AssignedPermission.class)
      .withName("security")
      .build();

    List<Map.Entry<String, AssignedPermission>> permissionIdsToRemove = securityStore
      .getAll()
      .entrySet()
      .stream()
      .filter(this::shouldPermissionBeRemoved)
      .toList();

    permissionIdsToRemove.forEach(permission -> {
      log.debug("removing permission {} from {}", permission.getKey(), permission.getValue().getName());
      securityStore.remove(permission.getKey());
    });
  }

  private boolean shouldPermissionBeRemoved(Map.Entry<String, AssignedPermission> entry) {
    return entry.getValue().getPermission().getValue().contains("readCIStatus");
  }

  @Override
  public Version getTargetVersion() {
    return Version.parse("3.1.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.security.xml";
  }
}
