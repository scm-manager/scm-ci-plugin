package com.cloudogu.scm.ci.cistatus.api;

import de.otto.edison.hal.HalRepresentation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@AllArgsConstructor
@Getter
@Setter
public class CIStatusDtoCollection extends HalRepresentation {
  private Collection<CIStatusDto> ciStatusDtos;
}
