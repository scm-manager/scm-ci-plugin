package com.cloudogu.scm.ci.cistatus.protocolcommand;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.ScmCommandProtocol;

import javax.inject.Inject;
import java.io.IOException;

public class CIStatusCommandProtocol implements ScmCommandProtocol {
  private CIStatusService service;

  @Inject
  public CIStatusCommandProtocol(CIStatusService service) {
    this.service = service;
  }

  @Override
  public void handle(CommandContext context, RepositoryContext repositoryContext) throws IOException {
    service.put(repositoryContext.getRepository(), context.getArgs()[1], new CIStatus());
  }
}
