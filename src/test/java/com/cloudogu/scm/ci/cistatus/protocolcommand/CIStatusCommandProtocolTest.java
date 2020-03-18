/**
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cloudogu.scm.ci.cistatus.protocolcommand;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.ci.cistatus.service.Status;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.repository.RepositoryTestData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CIStatusCommandProtocolTest {

  private final String SSH_COMMAND_WITHOUT_REVISION = "scm ci-update --namespace space --name name";
  private final String SSH_COMMAND = SSH_COMMAND_WITHOUT_REVISION + " --revision 1a2b3c4d5e6f";

  @Mock
  CIStatusService service;

  CIStatusCommandProtocol commandProtocol;
  RepositoryContext repositoryContext;

  @BeforeEach
  void initCommandProtocol() {
    commandProtocol = new CIStatusCommandProtocol(service, new CIStatusUnmarshaller());
    repositoryContext = createRepositoryContext();
  }

  @Test
  void shouldUpdateCIStatus() throws FileNotFoundException {
    CommandContext commandContext = createCommandContext(SSH_COMMAND, "protocolcommand/cistatus/1a2b3c4d5e6f.xml");
    commandProtocol.handle(commandContext, repositoryContext);

    CIStatus ciStatus = new CIStatus("jenkins", "scm-plugin", "scm-plugin", Status.SUCCESS, "http://localhost:8080/jenkins/job/scm-plugin/11/");
    verify(service).put(repositoryContext.getRepository(), "1a2b3c4d5e6f", ciStatus);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionIfCouldNotUnmarshal() throws FileNotFoundException {
    CommandContext commandContext = createCommandContext(SSH_COMMAND,"protocolcommand/cistatus/1a2b3c4d5e6f_invalid.xml");
    assertThrows(IllegalArgumentException.class, () -> commandProtocol.handle(commandContext, repositoryContext));
  }

  @Test
  void shouldThrowIllegalArgumentExceptionIfRevisionIsMissing() throws FileNotFoundException {
    CommandContext commandContext = createCommandContext(SSH_COMMAND_WITHOUT_REVISION,"protocolcommand/cistatus/1a2b3c4d5e6f.xml");
    assertThrows(IllegalArgumentException.class, () -> commandProtocol.handle(commandContext, repositoryContext));
  }

  private RepositoryContext createRepositoryContext() {
    return new RepositoryContext(RepositoryTestData.createHeartOfGold(), new File("").toPath());
  }

  private CommandContext createCommandContext(String sshCommand, String filename) throws FileNotFoundException {
    return new CommandContext(sshCommand, CIStatusCommandParser.parse(sshCommand), createInputStreamFromTestdata(filename), null, null);
  }

  private InputStream createInputStreamFromTestdata(String filename) throws FileNotFoundException {
    return new FileInputStream(Resources.getResource(filename).getFile());
  }
}
