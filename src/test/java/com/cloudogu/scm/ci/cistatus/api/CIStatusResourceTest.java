package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.ci.cistatus.service.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Repository;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static sonia.scm.repository.RepositoryTestData.createHeartOfGold;

@ExtendWith(MockitoExtension.class)
class CIStatusResourceTest {

  @Mock
  private CIStatusService ciStatusService;

  @Mock
  private CIStatusMapper mapper;

  private Repository repository = createHeartOfGold();
  private String changesetId = "42";

  @Test
  void shouldGetAll() {
    CIStatusCollection ciStatusCollection = new CIStatusCollection();
    CIStatus ciStatusOne = new CIStatus("jenkins", "build1", Status.PENDING, "http://test.de");
    ciStatusCollection.put(ciStatusOne);
    CIStatus ciStatusTwo = new CIStatus("jenkins", "build2", Status.PENDING, "http://test.de");
    ciStatusCollection.put(ciStatusTwo);

    CIStatusDto dtoOne = new CIStatusDto();
    doReturn(dtoOne).when(mapper).map(repository, changesetId, ciStatusOne);
    CIStatusDto dtoTwo = new CIStatusDto();
    doReturn(dtoTwo).when(mapper).map(repository, changesetId, ciStatusTwo);

    when(ciStatusService.get(repository, changesetId)).thenReturn(ciStatusCollection);

    CIStatusResource ciStatusResource = new CIStatusResource(ciStatusService, mapper, repository, changesetId);

    CIStatusDtoCollection ciStatusDtoCollection = ciStatusResource.getAll();
    assertThat(ciStatusDtoCollection.getCiStatusDtos()).contains(dtoOne, dtoTwo);
  }

  @Test
  void shouldGet() {
    String type = "sonartype";
    String ciName = "analyze1";

    CIStatusCollection ciStatusCollection = new CIStatusCollection();
    CIStatus ciStatusOne = new CIStatus(type, ciName, Status.PENDING, "http://test.de");
    ciStatusCollection.put(ciStatusOne);

    CIStatusDto dtoOne = new CIStatusDto();
    doReturn(dtoOne).when(mapper).map(repository, changesetId, ciStatusOne);

    when(ciStatusService.get(repository, changesetId)).thenReturn(ciStatusCollection);

    CIStatusResource ciStatusResource = new CIStatusResource(ciStatusService, mapper, repository, changesetId);
    CIStatusDto ciStatus = ciStatusResource.get(type, ciName);

    assertThat(ciStatus).isSameAs(dtoOne);
  }

  @Test
  void shouldPut() {
    String type = "sonartype";
    String ciName = "analyze1";

    CIStatusCollection ciStatusCollection = new CIStatusCollection();
    CIStatusDto dtoOne = new CIStatusDto();
    dtoOne.setName(ciName);
    dtoOne.setType(type);

    CIStatus ciStatusOne = new CIStatus(type, ciName, Status.PENDING, "http://test.de");

    when(mapper.map(dtoOne)).thenReturn(ciStatusOne);
    when(ciStatusService.get(repository, changesetId)).thenReturn(ciStatusCollection);

    CIStatusResource ciStatusResource = new CIStatusResource(ciStatusService, mapper, repository, changesetId);

    Response response = ciStatusResource.put(type, ciName, dtoOne);

    assertThat(ciStatusCollection).contains(ciStatusOne);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_NO_CONTENT);
    verify(ciStatusService).put(repository, changesetId, ciStatusCollection);
  }

  @Test
  void shouldReturnBadRequest() {
    String type = "sonartype";
    String ciName = "analyze1";

    CIStatusDto dtoOne = new CIStatusDto();
    dtoOne.setName("analyze2");

    CIStatusResource ciStatusResource = new CIStatusResource(ciStatusService, mapper, repository, changesetId);

    Response response = ciStatusResource.put(type, ciName, dtoOne);
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
  }
}
