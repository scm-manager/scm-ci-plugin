package com.cloudogu.scm.ci.cistatus.protocolcommand;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.protocolcommand.CommandInterpreter;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CIStatusCommandInterpreterFactoryTest {

  private CIStatusCommandInterpreterFactory factory;

  @Mock
  CIStatusRepositoryContextResolver repositoryContextResolver;
  @Mock
  CIStatusCommandProtocol commandProtocol;

  @Test
  void shouldReturnCommandInterpreter() {
    factory = new CIStatusCommandInterpreterFactory(repositoryContextResolver, commandProtocol);
    Optional<CommandInterpreter> commandInterpreter = factory.canHandle("scm ci-update */*/*");
    assertThat(commandInterpreter.isPresent()).isTrue();
  }

  @Test
  void shouldReturnEmptyIfCommandDoesNotMatch() {
    factory = new CIStatusCommandInterpreterFactory(repositoryContextResolver, commandProtocol);
    Optional<CommandInterpreter> commandInterpreter = factory.canHandle("scm ci update */*/*");
    assertThat(commandInterpreter.isPresent()).isFalse();
  }
}
