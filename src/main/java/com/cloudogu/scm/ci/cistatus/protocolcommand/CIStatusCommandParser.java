package com.cloudogu.scm.ci.cistatus.protocolcommand;

public class CIStatusCommandParser {

  static String[] parse (String command) {
    return command.split(" ");
  }
}
