package com.cloudogu.scm.ci.cistatus.protocolcommand;

class CIStatusCommandParser {

  private CIStatusCommandParser() {
  }

  static String[] parse(String command) {
    return command.split(" ");
  }
}
