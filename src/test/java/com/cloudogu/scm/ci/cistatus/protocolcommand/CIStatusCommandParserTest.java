package com.cloudogu.scm.ci.cistatus.protocolcommand;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CIStatusCommandParserTest {

  @Test
  void shouldSplitCommandArgumentsBySpaces() {
    String[] args = CIStatusCommandParser.parse("scm ci-update --namespace space --name name --revision 1a2b3c4d5e6f");
    assertThat(args.length).isEqualTo(8);
    assertThat(args[1]).isEqualToIgnoringCase("ci-update");
    assertThat(args[7]).isEqualToIgnoringCase("1a2b3c4d5e6f");
  }
}
