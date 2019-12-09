package com.cloudogu.scm.ci.cistatus.protocolcommand;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.ScmCommandProtocol;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
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
      JAXBContext jaxbContext = newInstance(CIStatus.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      InputStream in = context.getInputStream();
      CIStatus ciStatus = (CIStatus) unmarshaller.unmarshal(in);
      service.put(repositoryContext.getRepository(), extractRevision(context), ciStatus);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String extractRevision(CommandContext context) {
    String[] args = context.getArgs();
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--revision")) {
        return args[i + 1];
      }
    }
    throw new IllegalArgumentException("missing revision");
  }
}
