package com.cloudogu.scm.ci.cistatus.workflow;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
class BasicConfigration {
  private boolean ignoreChangesetStatus;

  @XmlTransient
  public String getContext() {
    return ignoreChangesetStatus ? "ignoreChangesetStatus" : "includeChangesetStatus";
  }

  public void setContext(String toBeIgnored) {
    // nothing to be done here
  }
}
