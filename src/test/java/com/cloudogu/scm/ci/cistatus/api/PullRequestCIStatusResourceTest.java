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

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusMerger;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.ci.cistatus.service.Status;
import de.otto.edison.hal.HalRepresentation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Repository;

import static de.otto.edison.hal.Links.emptyLinks;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static sonia.scm.repository.RepositoryTestData.createHeartOfGold;

@ExtendWith(MockitoExtension.class)
class PullRequestCIStatusResourceTest {

  @Mock
  private CIStatusPathBuilder pathBuilder;
  @Mock
  private CIStatusMapper mapper;
  @Mock
  private CIStatusService ciStatusService;
  @Mock
  private CIStatusMerger ciStatusMerger;

  @InjectMocks
  private CIStatusCollectionDtoMapper collectionDtoMapper;

  private final Repository repository = createHeartOfGold();
  private final String pullRequestId = "42";

  @Test
  void shouldGetAll() {
    when(pathBuilder.createChangesetCiStatusCollectionUri(repository.getNamespace(), repository.getName(), pullRequestId)).thenReturn("http://scm/status");

    CIStatusCollection ciStatusCollection = new CIStatusCollection();
    CIStatus ciStatusOne = new CIStatus("jenkins", "build1", null, Status.PENDING, "http://test.de");
    ciStatusCollection.put(ciStatusOne);
    CIStatus ciStatusTwo = new CIStatus("jenkins", "build2", null, Status.PENDING, "http://test.de");
    ciStatusCollection.put(ciStatusTwo);

    CIStatusDto dtoOne = new CIStatusDto(emptyLinks());
    doReturn(dtoOne).when(mapper).map(repository, pullRequestId, ciStatusOne);
    CIStatusDto dtoTwo = new CIStatusDto(emptyLinks());
    doReturn(dtoTwo).when(mapper).map(repository, pullRequestId, ciStatusTwo);

    when(ciStatusMerger.mergePullRequestCIStatuses(repository, pullRequestId)).thenReturn(ciStatusCollection);

    PullRequestCIStatusResource pullRequestCIStatusResource = new PullRequestCIStatusResource(ciStatusService, mapper, collectionDtoMapper, ciStatusMerger, repository, pullRequestId);

    HalRepresentation ciStatusCollectionEmbeddedWrapper = pullRequestCIStatusResource.getAll();
    assertThat(ciStatusCollectionEmbeddedWrapper.getEmbedded().getItemsBy("ciStatus")).contains(dtoOne, dtoTwo);
    assertThat(ciStatusCollectionEmbeddedWrapper.getLinks().getLinkBy("self").get().getHref()).isEqualTo("http://scm/status");
  }

  @Test
  void shouldGet() {
    String type = "sonartype";
    String ciName = "analyze1";

    CIStatusCollection ciStatusCollection = new CIStatusCollection();
    CIStatus ciStatusOne = new CIStatus(type, ciName, null, Status.PENDING, "http://test.de");
    ciStatusCollection.put(ciStatusOne);

    CIStatusDto dtoOne = new CIStatusDto(emptyLinks());
    doReturn(dtoOne).when(mapper).map(repository, pullRequestId, ciStatusOne);

    when(ciStatusService.get(CIStatusStore.PULL_REQUEST_STORE, repository, pullRequestId)).thenReturn(ciStatusCollection);

    PullRequestCIStatusResource pullRequestCIStatusResource = new PullRequestCIStatusResource(ciStatusService, mapper, collectionDtoMapper, ciStatusMerger, repository, pullRequestId);
    CIStatusDto ciStatus = pullRequestCIStatusResource.get(type, ciName);

    assertThat(ciStatus).isSameAs(dtoOne);
  }

  @Test
  void shouldPut() {
    String type = "sonartype";
    String ciName = "analyze1";

    CIStatusDto dtoOne = new CIStatusDto(emptyLinks());
    dtoOne.setName(ciName);
    dtoOne.setType(type);

    CIStatus ciStatusOne = new CIStatus(type, ciName, null, Status.PENDING, "http://test.de");

    when(mapper.map(dtoOne)).thenReturn(ciStatusOne);

    PullRequestCIStatusResource pullRequestCIStatusResource = new PullRequestCIStatusResource(ciStatusService, mapper, collectionDtoMapper, ciStatusMerger, repository, pullRequestId);

    Response response = pullRequestCIStatusResource.put(type, ciName, dtoOne);

    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_NO_CONTENT);
    verify(ciStatusService).put(CIStatusStore.PULL_REQUEST_STORE, repository, pullRequestId, ciStatusOne);
  }

}
