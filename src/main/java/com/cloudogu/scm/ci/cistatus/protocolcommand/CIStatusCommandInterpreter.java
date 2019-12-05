package com.cloudogu.scm.ci.cistatus.protocolcommand;

import sonia.scm.protocolcommand.CommandInterpreter;
import sonia.scm.protocolcommand.RepositoryContextResolver;
import sonia.scm.protocolcommand.ScmCommandProtocol;

public class CIStatusCommandInterpreter implements CommandInterpreter {
  private RepositoryContextResolver repositoryContextResolver;
  private ScmCommandProtocol protocolHandler;
  private String[] args;

  public CIStatusCommandInterpreter(RepositoryContextResolver repositoryContextResolver, ScmCommandProtocol protocolHandler, String[] args) {
    this.repositoryContextResolver = repositoryContextResolver;
    this.protocolHandler = protocolHandler;
    this.args = args;
  }

  @Override
  public String[] getParsedArgs() {
    return args;
  }

  @Override
  public ScmCommandProtocol getProtocolHandler() {
    return protocolHandler;
  }

  @Override
  public RepositoryContextResolver getRepositoryContextResolver() {
    return repositoryContextResolver;
  }
}
