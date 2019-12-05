package com.cloudogu.scm.ci.cistatus.protocolcommand;

import sonia.scm.plugin.Extension;
import sonia.scm.protocolcommand.CommandInterpreter;
import sonia.scm.protocolcommand.CommandInterpreterFactory;
import sonia.scm.protocolcommand.RepositoryContextResolver;
import sonia.scm.protocolcommand.ScmCommandProtocol;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Optional.of;

@Extension
public class CIStatusCommandInterpreterFactory implements CommandInterpreterFactory {
  private final RepositoryContextResolver repositoryContextResolver;
  private final ScmCommandProtocol protocolHandler;

  @Inject
  public CIStatusCommandInterpreterFactory(RepositoryContextResolver repositoryContextResolver, ScmCommandProtocol protocolHandler) {
    this.repositoryContextResolver = repositoryContextResolver;
    this.protocolHandler = protocolHandler;
  }

  @Override
  public Optional<CommandInterpreter> canHandle(String command) {
    String[] args = CIStatusCommandParser.parse(command);
    if (command.startsWith("scm ci")) {
      return of(new CIStatusCommandInterpreter(repositoryContextResolver, protocolHandler, args));
    }
    return Optional.empty();
  }
}
