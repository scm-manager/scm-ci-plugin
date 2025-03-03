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

package com.cloudogu.scm.ci;

import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

public final class PermissionCheck {

  private static final String WRITE_CI_STATUS = "writeCIStatus";

  private PermissionCheck() {
  }

  public static boolean mayRead(Repository repository) {
    return RepositoryPermissions.read(repository).isPermitted();
  }

  public static void checkRead(Repository repository) {
    RepositoryPermissions.read(repository).check();
  }

  public static boolean mayWrite(Repository repository) {
    return RepositoryPermissions.custom(WRITE_CI_STATUS, repository).isPermitted();
  }

  public static void checkWrite(Repository repository) {
    RepositoryPermissions.custom(WRITE_CI_STATUS, repository).check();
  }

}
