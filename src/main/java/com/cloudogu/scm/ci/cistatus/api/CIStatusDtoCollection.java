package com.cloudogu.scm.ci.cistatus.api;

import de.otto.edison.hal.HalRepresentation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collection;

@AllArgsConstructor
@Getter
@Setter
@XmlJavaTypeAdapter(CIStatusDtoCollection.CiStatusCollectionAdapter.class)
public class CIStatusDtoCollection extends HalRepresentation {

  private Collection<CIStatusDto> ciStatusDtos;

  /**
   * Adapter to unwrap the array ci status _embedded objects.
   */
  public static class CiStatusCollectionAdapter extends XmlAdapter<Collection<CIStatusDto>, CIStatusDtoCollection> {

    @Override
    public CIStatusDtoCollection unmarshal(Collection<CIStatusDto> v) {
      return new CIStatusDtoCollection(v);
    }

    @Override
    public Collection<CIStatusDto> marshal(CIStatusDtoCollection v) {
      return v.ciStatusDtos;
    }
  }
}
