/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
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

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CIStatusRepositoryContextResolverTest {

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
