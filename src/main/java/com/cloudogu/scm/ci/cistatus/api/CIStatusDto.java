package com.cloudogu.scm.ci.cistatus.api;


import com.cloudogu.scm.ci.cistatus.service.Status;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CIStatusDto extends HalRepresentation {
  private String name;
  private String type;
  private Status status;
  private String url;

  public CIStatusDto(Links links) {
    super(links);
  }
}
