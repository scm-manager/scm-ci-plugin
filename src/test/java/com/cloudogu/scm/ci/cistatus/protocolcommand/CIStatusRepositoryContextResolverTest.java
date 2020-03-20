/*
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryLocationResolver;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.RepositoryTestData;

import javax.naming.Name;
import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CIStatusRepositoryContextResolverTest {

  @Mock
  private RepositoryManager manager;
  @Mock (answer = Answers.RETURNS_DEEP_STUBS)
  private RepositoryLocationResolver locationResolver;
  @InjectMocks
  private CIStatusRepositoryContextResolver repositoryContextResolver;

  @Test
  void shouldResolveRepositoryContext() {
    String SSH_COMMAND = "scm ci-update --namespace space --name name --revision 1a2b3c4d5e6f";
    String[] args = CIStatusCommandParser.parse(SSH_COMMAND);

    NamespaceAndName namespaceAndName = new NamespaceAndName(args[3], args[5]);
    Repository repository = RepositoryTestData.createHeartOfGold();
    when(manager.get(namespaceAndName)).thenReturn(repository);
    when(locationResolver.forClass(Path.class).getLocation(repository.getId())).thenReturn(new File("").toPath());

    RepositoryContext context = repositoryContextResolver.resolve(args);

    verify(manager).get(namespaceAndName);
    assertThat(context.getRepository()).isEqualTo(repository);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionIfNamespaceOrNameIsMissing() {
    String SSH_COMMAND = "scm ci-update --revision 1a2b3c4d5e6f";
    String[] args = CIStatusCommandParser.parse(SSH_COMMAND);

    assertThrows(IllegalArgumentException.class, () -> repositoryContextResolver.resolve(args));
  }
}
