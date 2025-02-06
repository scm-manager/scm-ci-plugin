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

package com.cloudogu.scm.ci.deployment.service;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DeploymentCollection implements Iterable<Deployment> {

  private final Map<Key, Deployment> deployments = new HashMap<>();

  public void put(Deployment deployment) {
    deployments.put(new Key(deployment.getSource(), deployment.getEnvironment()), deployment);
  }

  public void remove(Key key) {
    deployments.remove(key);
  }

  @Override
  public Iterator<Deployment> iterator() {
    return deployments.values().iterator();
  }

  public Stream<Deployment> stream() {
    return deployments.values().stream();
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Key {
    private String source;
    private String environment;
  }
}
