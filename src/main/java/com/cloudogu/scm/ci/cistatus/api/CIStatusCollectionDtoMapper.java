package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
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
      new Links.Builder().self(ciStatusPathBuilder.createCollectionUri(repository.getNamespace(), repository.getName(), changesetId)).build(),
      Embedded.embedded("ciStatus", ciStatus
        .map(s -> mapper.map(repository, changesetId, s))
        .collect(Collectors.toList())));
  }
}
