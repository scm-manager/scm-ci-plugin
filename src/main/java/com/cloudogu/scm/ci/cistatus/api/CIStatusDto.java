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

    @Override
    @SuppressWarnings("squid:S1185") // We want to have this method available in this package
    protected HalRepresentation add(Links links) {
        return super.add(links);
    }
}
