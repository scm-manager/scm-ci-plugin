package com.cloudogu.scm.ci.cistatus.protocolcommand;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;

import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
