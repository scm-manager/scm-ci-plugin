package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sonia.scm.repository.Repository;

import javax.inject.Inject;

@Mapper
public abstract class CIStatusMapper {

  @Inject
  private CIStatusPathBuilder ciStatusPathBuilder;

    @Mapping(target = "attributes", ignore = true) // We do not map HAL attributes
    public abstract CIStatusDto map(@Context Repository repository, @Context String changeSetId, CIStatus ciStatus);

    public abstract CIStatus map(CIStatusDto ciStatusDto);

    @AfterMapping
    void appendLinks(@MappingTarget CIStatusDto target, @Context Repository repository, @Context String changesetId) {
      String namespace = repository.getNamespace();
      String name = repository.getName();
      final Links.Builder linksBuilder = new Links.Builder();
      linksBuilder.self(ciStatusPathBuilder.createCiStatusSelfUri(namespace, name, changesetId, target.getType(), target.getName()));
      target.add(linksBuilder.build());
    }
}
