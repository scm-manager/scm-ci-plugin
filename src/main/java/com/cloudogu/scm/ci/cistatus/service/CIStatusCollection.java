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

package com.cloudogu.scm.ci.cistatus.service;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

@XmlRootElement
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

  public Stream<CIStatus> stream() {
    return ciStatusList.values().stream();
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  private static final class Key {
    private String type;
    private String name;
  }
}

