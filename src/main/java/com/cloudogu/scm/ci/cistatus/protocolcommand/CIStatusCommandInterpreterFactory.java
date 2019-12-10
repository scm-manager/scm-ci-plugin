package com.cloudogu.scm.ci.cistatus.protocolcommand;

import sonia.scm.plugin.Extension;
import sonia.scm.protocolcommand.CommandInterpreter;
import sonia.scm.protocolcommand.CommandInterpreterFactory;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Optional.of;

@Extension
public class CIStatusCommandInterpreterFactory implements CommandInterpreterFactory {
  private final CIStatusRepositoryContextResolver repositoryContextResolver;
  private final CIStatusCommandProtocol protocolHandler;

  @Inject
  public CIStatusCommandInterpreterFactory(CIStatusRepositoryContextResolver repositoryContextResolver, CIStatusCommandProtocol protocolHandler) {
    this.repositoryContextResolver = repositoryContextResolver;
    this.protocolHandler = protocolHandler;
  }

  @Override
  public Optional<CommandInterpreter> canHandle(String command) {
    if (command.startsWith("scm ci-update")) {
      return of(new CIStatusCommandInterpreter(repositoryContextResolver, protocolHandler, CIStatusCommandParser.parse(command)));
    }
    return Optional.empty();
  }
}
