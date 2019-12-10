package com.cloudogu.scm.ci.cistatus.protocolcommand;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.ScmCommandProtocol;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

import static javax.xml.bind.JAXBContext.newInstance;

public class CIStatusCommandProtocol implements ScmCommandProtocol {
  private CIStatusService service;

  @Inject
  public CIStatusCommandProtocol(CIStatusService service) {
    this.service = service;
  }

  @Override
  public void handle(CommandContext context, RepositoryContext repositoryContext) {
    try {
      CIStatus ciStatus = unmarshalCIStatus(context);
      service.put(repositoryContext.getRepository(), extractRevision(context), ciStatus);
    } catch (JAXBException e) {
      throw new IllegalArgumentException("could not unmarshal ciStatus object");
    }
  }

  private CIStatus unmarshalCIStatus(CommandContext context) throws JAXBException {
    JAXBContext jaxbContext = newInstance(CIStatus.class);
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    InputStream in = context.getInputStream();
    return (CIStatus) unmarshaller.unmarshal(in);
  }

  private String extractRevision(CommandContext context) {
    String[] args = context.getArgs();
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--revision")) {
        return args[i + 1];
      }
    }
    throw new IllegalArgumentException("missing revision in scm ci-update command");
  }
}
