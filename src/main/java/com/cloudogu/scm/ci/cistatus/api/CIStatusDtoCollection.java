package com.cloudogu.scm.ci.cistatus.api;

import de.otto.edison.hal.HalRepresentation;
import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
public class CIStatusDtoCollection extends HalRepresentation {
  private Collection<CIStatusDto> ciStatusDtos;
}
