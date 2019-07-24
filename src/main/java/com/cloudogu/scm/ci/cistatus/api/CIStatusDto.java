package com.cloudogu.scm.ci.cistatus.api;


import com.cloudogu.scm.ci.cistatus.service.Status;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class CIStatusDto extends HalRepresentation {
  @NotNull
  @Size(min = 1)
  private String name;
  @NotNull
  @Size(min = 1)
  private String type;
  @NotNull
  private Status status;
  @NotNull
  @Size(min = 1)
  private String url;

  public CIStatusDto(Links links) {
    super(links);
  }
}
