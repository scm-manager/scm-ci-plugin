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

package com.cloudogu.scm.ci;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.NotFoundException;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.RepositoryTestData;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryResolverTest {

  @Mock
  private RepositoryManager repositoryManager;

  @InjectMocks
  private RepositoryResolver resolver;

  @Test
  void shouldResolveRepository() {
    NamespaceAndName namespaceAndName = new NamespaceAndName("hitchhicker", "heart-of-gold");
    when(repositoryManager.get(namespaceAndName)).thenReturn(RepositoryTestData.createHeartOfGold());

    Repository repository = resolver.resolve("hitchhicker", "heart-of-gold");

    assertNotNull(repository);
  }

  @Test
  void shouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> resolver.resolve("not", "found"));
  }

}
