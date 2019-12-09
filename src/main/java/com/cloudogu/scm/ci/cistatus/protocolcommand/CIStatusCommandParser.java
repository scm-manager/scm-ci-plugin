package com.cloudogu.scm.ci.cistatus.protocolcommand;

import sun.tools.jar.CommandLine;

import java.io.IOException;

public class CIStatusCommandParser {

  static String[] parse (String command) throws IOException {
    return CommandLine.parse(command.split(" "));
  }
}
