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

package com.cloudogu.scm.ci.cistatus.service;

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import lombok.Getter;
import sonia.scm.event.Event;
import sonia.scm.repository.Repository;

@Event
@Getter
public class CIStatusEvent {

  private final CIStatusStore type;
  private final Repository repository;
  private final String id;
  private final CIStatus ciStatus;

  public CIStatusEvent(Repository repository, CIStatusStore store, String id, CIStatus ciStatus) {
    this.type = store;
    this.repository = repository;
    this.id = id;
    this.ciStatus = ciStatus;
  }
}
