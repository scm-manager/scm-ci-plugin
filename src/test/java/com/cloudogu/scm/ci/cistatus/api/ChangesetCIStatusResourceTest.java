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

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.ci.cistatus.service.Status;
import de.otto.edison.hal.HalRepresentation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.IllegalIdentifierChangeException;
import sonia.scm.repository.Repository;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import static de.otto.edison.hal.Links.emptyLinks;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static sonia.scm.repository.RepositoryTestData.createHeartOfGold;

@ExtendWith(MockitoExtension.class)
class ChangesetCIStatusResourceTest {

  @Mock
  private CIStatusService ciStatusService;

  @Mock
  private CIStatusMapper mapper;

  @Mock
  private CIStatusPathBuilder pathBuilder;

  @InjectMocks
  private CIStatusCollectionDtoMapper collectionDtoMapper;

  private Repository repository = createHeartOfGold();
  private String changesetId = "42";

  @Test
  void shouldGetAll() {
    when(pathBuilder.createChangesetCiStatusCollectionUri(repository.getNamespace(), repository.getName(), changesetId)).thenReturn("http://scm/status");

    CIStatusCollection ciStatusCollection = new CIStatusCollection();
    CIStatus ciStatusOne = new CIStatus("jenkins", "build1", null, Status.PENDING, "http://test.de");
    ciStatusCollection.put(ciStatusOne);
    CIStatus ciStatusTwo = new CIStatus("jenkins", "build2", null, Status.PENDING, "http://test.de");
    ciStatusCollection.put(ciStatusTwo);

    CIStatusDto dtoOne = new CIStatusDto(emptyLinks());
    doReturn(dtoOne).when(mapper).map(repository, changesetId, ciStatusOne);
    CIStatusDto dtoTwo = new CIStatusDto(emptyLinks());
    doReturn(dtoTwo).when(mapper).map(repository, changesetId, ciStatusTwo);

    when(ciStatusService.get(CIStatusStore.CHANGESET_STORE, repository, changesetId)).thenReturn(ciStatusCollection);

    ChangesetCIStatusResource changesetCiStatusResource = new ChangesetCIStatusResource(ciStatusService, mapper, collectionDtoMapper, repository, changesetId);

    HalRepresentation ciStatusCollectionEmbeddedWrapper = changesetCiStatusResource.getAll();
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
    doReturn(dtoOne).when(mapper).map(repository, changesetId, ciStatusOne);

    when(ciStatusService.get(CIStatusStore.CHANGESET_STORE, repository, changesetId)).thenReturn(ciStatusCollection);

    ChangesetCIStatusResource changesetCiStatusResource = new ChangesetCIStatusResource(ciStatusService, mapper, collectionDtoMapper, repository, changesetId);
    CIStatusDto ciStatus = changesetCiStatusResource.get(type, ciName);

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

    ChangesetCIStatusResource changesetCiStatusResource = new ChangesetCIStatusResource(ciStatusService, mapper, collectionDtoMapper, repository, changesetId);

    Response response = changesetCiStatusResource.put(type, ciName, dtoOne);

    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_NO_CONTENT);
    verify(ciStatusService).put(CIStatusStore.CHANGESET_STORE, repository, changesetId, ciStatusOne);
  }

  @Test
  void shouldThrowIllegalIdentifierChangeException() {
    String type = "sonartype";
    String ciName = "analyze1";

    CIStatusDto dtoOne = new CIStatusDto(emptyLinks());
    dtoOne.setName(ciName);
    dtoOne.setType(type);

    ChangesetCIStatusResource changesetCiStatusResource = new ChangesetCIStatusResource(ciStatusService, mapper, collectionDtoMapper, repository, changesetId);

    assertThrows(IllegalIdentifierChangeException.class, () -> changesetCiStatusResource.put("jenkins", ciName, dtoOne));
  }
}
