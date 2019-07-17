package com.cloudogu.scm.ci;

import org.apache.shiro.authz.UnauthorizedException;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

public final class PermissionCheck {

  public static final String READ_CI_STATUS = "readCIStatus";
  public static final String WRITE_CI_STATUS = "writeCIStatus";

  private PermissionCheck() {
  }

  public static boolean mayRead(Repository repository) {
    return RepositoryPermissions.custom(READ_CI_STATUS, repository).isPermitted();
  }

  public static void checkRead(Repository repository) {
    if (!mayRead(repository)) {
      String msg = "User is not permitted to read ci status";
      throw new UnauthorizedException(msg);
    }
  }

  public static boolean mayWrite(Repository repository) {
    return RepositoryPermissions.custom(WRITE_CI_STATUS, repository).isPermitted();
  }

  public static void checkWrite(Repository repository) {
    if (!mayWrite(repository)) {
      String msg = "User is not permitted to write ci status";
      throw new UnauthorizedException(msg);
    }
  }

}
