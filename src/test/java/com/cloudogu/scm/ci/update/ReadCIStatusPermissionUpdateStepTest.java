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
import sonia.scm.security.AssignedPermission;
import sonia.scm.store.ConfigurationEntryStore;
import sonia.scm.store.InMemoryByteConfigurationEntryStoreFactory;

import static org.assertj.core.api.Assertions.assertThat;

class ReadCIStatusPermissionUpdateStepTest {

  private final InMemoryByteConfigurationEntryStoreFactory entryStoreFactory = new InMemoryByteConfigurationEntryStoreFactory();
  private ReadCIStatusPermissionUpdateStep step;

  @BeforeEach
  void setup() {
    step = new ReadCIStatusPermissionUpdateStep(entryStoreFactory);
  }

  @Test
  void shouldOnlyRemoveReadCIStatusPermissionFromUsersAndGroups() {
    ConfigurationEntryStore<AssignedPermission> securityStore = entryStoreFactory
      .withType(AssignedPermission.class)
      .withName("security")
      .build();

    AssignedPermission userAdminPermission = new AssignedPermission("Trainer Red", false, "*");
    AssignedPermission userRepositoryReadPermission = new AssignedPermission("Trainer Red", false, "repository:read:*");
    AssignedPermission userJenkinsConfigPermission = new AssignedPermission("Trainer Red", false, "configuration:read,write:jenkins");
    AssignedPermission userReadCIStatusPermission = new AssignedPermission("Trainer Red", false, "repository:readCIStatus:*");
    securityStore.put(userAdminPermission);
    securityStore.put(userRepositoryReadPermission);
    securityStore.put(userJenkinsConfigPermission);
    securityStore.put(userReadCIStatusPermission);

    AssignedPermission groupAdminPermission = new AssignedPermission("Elite Four", true, "*");
    AssignedPermission groupRepositoryReadPermission = new AssignedPermission("Elite Four", true, "repository:read:*");
    AssignedPermission groupJenkinsConfigPermission = new AssignedPermission("Elite Four", true, "configuration:read,write:jenkins");
    AssignedPermission groupReadCIStatusPermission = new AssignedPermission("Elite Four", true, "repository:readCIStatus:*");
    securityStore.put(groupAdminPermission);
    securityStore.put(groupRepositoryReadPermission);
    securityStore.put(groupJenkinsConfigPermission);
    securityStore.put(groupReadCIStatusPermission);

    step.doUpdate();

    assertThat(securityStore.getAll().values())
      .hasSize(6)
      .containsOnly(
        userAdminPermission,
        userRepositoryReadPermission,
        userJenkinsConfigPermission,
        groupAdminPermission,
        groupRepositoryReadPermission,
        groupJenkinsConfigPermission
      );
  }
}
