package com.cloudogu.scm.ci.cistatus.protocolcommand;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.ScmCommandProtocol;

import javax.inject.Inject;

public class CIStatusCommandProtocol implements ScmCommandProtocol {

  private final CIStatusService service;
  private final CIStatusUnmarshaller unmarshaller;

  @Inject
  public CIStatusCommandProtocol(CIStatusService service, CIStatusUnmarshaller unmarshaller) {
    this.service = service;
    this.unmarshaller = unmarshaller;
  }

  @Override
  public void handle(CommandContext context, RepositoryContext repositoryContext) {
      CIStatus ciStatus = unmarshaller.unmarshal(context.getInputStream());
      service.put(repositoryContext.getRepository(), extractRevision(context), ciStatus);
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
