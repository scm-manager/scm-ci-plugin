package com.cloudogu.scm.ci;

import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

public final class PermissionCheck {

  private static final String READ_CI_STATUS = "readCIStatus";
  private static final String WRITE_CI_STATUS = "writeCIStatus";

  private PermissionCheck() {
  }

  public static boolean mayRead(Repository repository) {
    return RepositoryPermissions.custom(READ_CI_STATUS, repository).isPermitted();
  }

  public static void checkRead(Repository repository) {
    RepositoryPermissions.custom(READ_CI_STATUS, repository).check();
  }

  public static boolean mayWrite(Repository repository) {
    return RepositoryPermissions.custom(WRITE_CI_STATUS, repository).isPermitted();
  }

  public static void checkWrite(Repository repository) {
    RepositoryPermissions.custom(WRITE_CI_STATUS, repository).check();
  }

}
