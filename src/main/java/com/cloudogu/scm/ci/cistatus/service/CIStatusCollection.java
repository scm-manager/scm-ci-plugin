package com.cloudogu.scm.ci.cistatus.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class CIStatusCollection implements Iterable<CIStatus> {

  private final Map<Key, CIStatus> ciStatusList = new LinkedHashMap<>();

  public void put(CIStatus ciStatus) {
    ciStatusList.put(new Key(ciStatus.getType(), ciStatus.getName()), ciStatus);
  }

  public CIStatus get(String type, String name) {
    return ciStatusList.get(new Key(type, name));
  }

  @Override
  public Iterator<CIStatus> iterator() {
    return ciStatusList.values().iterator();
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  private static final class Key {
    private String type;
    private String name;
  }
}

