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
package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    ChangesetCIStatusResource resource = rootResource.getCIStatusResource("hitchhiker", "heart-of-gold", "42");

    assertThat(resource.getRepository()).isSameAs(repository);
    assertThat(resource.getChangesetId()).isEqualTo("42");
  }
}
