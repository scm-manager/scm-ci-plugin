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

package com.cloudogu.scm.ci.cistatus.workflow;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
class BasicConfigration {
  private boolean ignoreChangesetStatus;

  /**
   * @implNote Used as a suffix for frontend translation keys. The name `context` is required.
   */
  @XmlTransient
  public String getContext() {
    return ignoreChangesetStatus ? "ignoreChangesetStatus" : "includeChangesetStatus";
  }

  public void setContext(String toBeIgnored) {
    // nothing to be done here
  }
}
