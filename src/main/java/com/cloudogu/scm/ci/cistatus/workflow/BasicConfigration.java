package com.cloudogu.scm.ci.cistatus.workflow;

import lombok.Getter;

@Getter
class BasicConfigration {
  private boolean ignoreChangesetStatus;

  public String getContext() {
    return ignoreChangesetStatus ? "ignoreChangesetStatus" : "includeChangesetStatus";
  }

  public void setContext(String toBeIgnored) {
    // nothing to be done here
  }
}
