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
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.ScmCommandProtocol;

import javax.inject.Inject;

public class CIStatusCommandProtocol implements ScmCommandProtocol {

  private final CIStatusService service;
  private final CIStatusUnmarshaller unmarshaller;

  @Inject
  public CIStatusCommandProtocol(CIStatusService service, CIStatusUnmarshaller unmarshaller) {
    this.service = service;
    this.unmarshaller = unmarshaller;
  }

  @Override
  public void handle(CommandContext context, RepositoryContext repositoryContext) {
      CIStatus ciStatus = unmarshaller.unmarshal(context.getInputStream());
      service.put(repositoryContext.getRepository(), extractRevision(context), ciStatus);
  }

  private String extractRevision(CommandContext context) {
    String[] args = context.getArgs();
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--revision")) {
        return args[i + 1];
      }
    }
    throw new IllegalArgumentException("missing revision in scm ci-update command");
  }
}
