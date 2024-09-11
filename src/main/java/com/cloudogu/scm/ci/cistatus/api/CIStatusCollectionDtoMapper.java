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

package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import jakarta.inject.Inject;
import sonia.scm.repository.Repository;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CIStatusCollectionDtoMapper {

  private final CIStatusMapper mapper;
  private final CIStatusPathBuilder ciStatusPathBuilder;

  @Inject
  public CIStatusCollectionDtoMapper(CIStatusMapper mapper, CIStatusPathBuilder ciStatusPathBuilder) {
    this.mapper = mapper;
    this.ciStatusPathBuilder = ciStatusPathBuilder;
  }

  HalRepresentation map(Stream<CIStatus> ciStatus, Repository repository, String changesetId) {
    return new HalRepresentation(
      new Links.Builder().self(ciStatusPathBuilder.createChangesetCiStatusCollectionUri(repository.getNamespace(), repository.getName(), changesetId)).build(),
      Embedded.embedded("ciStatus", ciStatus
        .map(s -> mapper.map(repository, changesetId, s))
        .collect(Collectors.toList())));
  }
}
