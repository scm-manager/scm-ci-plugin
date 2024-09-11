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

package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.NotFoundException;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangesetCIStatusRootResourceTest {

  @Mock
  private CIStatusService service;

  @Mock
  private CIStatusMapper mapper;

  @Mock
  private RepositoryResolver resolver;

  @Mock
  private RepositoryServiceFactory repositoryServiceFactory;

  @Mock
  private LogCommandBuilder logCommandBuilder;

  @Mock
  private RepositoryService repositoryService;

  @InjectMocks
  private ChangesetCIStatusRootResource rootResource;

  @Test
  void shouldReturnCiStatusResource() throws IOException {
    Repository repository = RepositoryTestData.createHeartOfGold();
    Changeset changeset = new Changeset();
    changeset.setId("42");
    when(resolver.resolve("hitchhiker", "heart-of-gold")).thenReturn(repository);
    when(repositoryServiceFactory.create(any(Repository.class))).thenReturn(repositoryService);
    when(repositoryService.getLogCommand()).thenReturn(logCommandBuilder);
    when(logCommandBuilder.getChangeset("42")).thenReturn(changeset);

    ChangesetCIStatusResource resource = rootResource.getChangesetCIStatusResource("hitchhiker", "heart-of-gold", "42");

    assertThat(resource.getRepository()).isSameAs(repository);
    assertThat(resource.getChangesetId()).isEqualTo("42");
  }

  @Test
  void shouldFailForUnknownRevision() throws IOException {
    Repository repository = RepositoryTestData.createHeartOfGold();
    when(resolver.resolve("hitchhiker", "heart-of-gold")).thenReturn(repository);
    when(repositoryServiceFactory.create(any(Repository.class))).thenReturn(repositoryService);
    when(repositoryService.getLogCommand()).thenReturn(logCommandBuilder);
    when(logCommandBuilder.getChangeset("42")).thenReturn(null);

    Assert.assertThrows(NotFoundException.class, () -> rootResource.getChangesetCIStatusResource("hitchhiker", "heart-of-gold", "42"));
  }
}
