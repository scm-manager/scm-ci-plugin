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
import de.otto.edison.hal.Links;
import jakarta.inject.Inject;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import sonia.scm.repository.Repository;

@Mapper
public abstract class CIStatusMapper {

  @Inject
  private CIStatusPathBuilder ciStatusPathBuilder;

  @Mapping(target = "attributes", ignore = true) // We do not map HAL attributes
  public abstract CIStatusDto map(@Context Repository repository, @Context String changesetId, CIStatus ciStatus);

  public abstract CIStatus map(CIStatusDto ciStatusDto);

  @ObjectFactory
  CIStatusDto createDto(CIStatus ciStatus, @Context Repository repository, @Context String changesetId) {
    String namespace = repository.getNamespace();
    String name = repository.getName();
    final Links.Builder linksBuilder = new Links.Builder();
    linksBuilder.self(ciStatusPathBuilder.createChangesetCiStatusSelfUri(namespace, name, changesetId, ciStatus.getType(), ciStatus.getName()));
    return new CIStatusDto(linksBuilder.build());
  }
}
