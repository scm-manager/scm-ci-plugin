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

package com.cloudogu.scm.ci.cistatus.protocolcommand;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import jakarta.inject.Singleton;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.InputStream;

@Singleton
public class CIStatusUnmarshaller {

  private final JAXBContext context;

  public CIStatusUnmarshaller() {
    try {
      context = JAXBContext.newInstance(CIStatus.class);
    } catch (JAXBException e) {
      throw new IllegalStateException("failed to create jaxb context for ci status", e);
    }
  }

  public CIStatus unmarshal(InputStream input) {
    try {
      Unmarshaller unmarshaller = context.createUnmarshaller();
      return (CIStatus) unmarshaller.unmarshal(input);
    } catch (JAXBException ex) {
      throw new IllegalArgumentException("failed to unmarshal input stream", ex);
    }
  }
}
