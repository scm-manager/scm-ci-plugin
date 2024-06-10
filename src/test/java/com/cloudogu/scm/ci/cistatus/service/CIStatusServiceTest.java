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
package com.cloudogu.scm.ci.cistatus.service;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.event.ScmEventBus;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.store.InMemoryDataStore;
import sonia.scm.store.TypedStoreParameters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.cloudogu.scm.ci.cistatus.CIStatusStore.CHANGESET_STORE;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static sonia.scm.repository.RepositoryTestData.createRestaurantAtTheEndOfTheUniverse;

@ExtendWith(MockitoExtension.class)
class CIStatusServiceTest {

  public static final Repository REPOSITORY = new Repository("42", "git", "hitchhiker", "hog");

  @Mock
  private RepositoryServiceFactory repositoryServiceFactory;
  @Mock
  private ScmEventBus eventBus;

  private CIStatusService ciStatusService;

  @Nested
  class WithoutPermission {

    @Mock
    private Subject subject;

    @BeforeEach
    void setUpDataStoreFactory() {
      ciStatusService = new CIStatusService(new TestingDataStoreFactory(), repositoryServiceFactory, eventBus);
      ThreadContext.bind(subject);
      doThrow(new UnauthorizedException()).when(subject).checkPermission(any(String.class));
    }

    @AfterEach
    void tearDownSubject() {
      ThreadContext.unbindSubject();
    }

    @Test
    void shouldThrowAuthorizationExceptionWhenMissingPermissions() {
      Repository repository = REPOSITORY;

      assertThrows(UnauthorizedException.class, () -> ciStatusService.get(CHANGESET_STORE, repository, "1234"));
      assertThrows(UnauthorizedException.class, () -> ciStatusService.put(CHANGESET_STORE, repository, "1234", new CIStatus()));
    }
  }

  @Nested
  class WithPermission {

    @Mock
    private Subject subject;

    @BeforeEach
    void setUpDataStoreFactory() {
      ciStatusService = new CIStatusService(new TestingDataStoreFactory(), repositoryServiceFactory, eventBus);
      ThreadContext.bind(subject);
      lenient().when(subject.isPermitted(any(String.class))).thenReturn(true);
    }

    @AfterEach
    void tearDownSubject() {
      ThreadContext.unbindSubject();
    }

    @Test
    void shouldReturnEmptyCollection() {
      CIStatusCollection ciStatusCollection = ciStatusService.get(CHANGESET_STORE, REPOSITORY, "1234");
      assertThat(ciStatusCollection).isNotNull().isEmpty();
    }

    @Test
    void shouldPutAndGetOneRepositoryAndTwoChangesets() {
      CIStatus ciStatus = new CIStatus("test", "name", null, Status.PENDING, "http://abc.de");
      ciStatusService.put(CHANGESET_STORE, REPOSITORY, "123456", ciStatus);
      ciStatusService.put(CHANGESET_STORE, REPOSITORY, "654321", ciStatus);

      CIStatusCollection result = ciStatusService.get(CHANGESET_STORE, REPOSITORY, "123456");

      assertThat(result.get("test", "name")).isSameAs(ciStatus);
    }

    @Test
    void shouldPutAndGetTwoRepositoriesAndOneChangesetEach() {
      Repository repository2 = createRestaurantAtTheEndOfTheUniverse();
      repository2.setId("24");

      CIStatus ciStatus1 = new CIStatus("test", "name", null, Status.PENDING, "http://abc.de");
      ciStatusService.put(CHANGESET_STORE, REPOSITORY, "123456", ciStatus1);
      CIStatus ciStatus2 = new CIStatus("test2", "name2", null, Status.PENDING, "http://abc.de");
      ciStatusService.put(CHANGESET_STORE, repository2, "654321", ciStatus2);

      CIStatusCollection resultWithRepo1 = ciStatusService.get(CHANGESET_STORE, REPOSITORY, "123456");
      CIStatusCollection resultWithRepo2 = ciStatusService.get(CHANGESET_STORE, repository2, "654321");

      assertThat(resultWithRepo1.get("test", "name")).isSameAs(ciStatus1);
      assertThat(resultWithRepo2.get("test2", "name2")).isSameAs(ciStatus2);
    }

    @Test
    void shouldPostEvent() {
      CIStatus ciStatus = new CIStatus("test", "name", "displayName", Status.SUCCESS, "http://hog.org/", "nothing");

      ciStatusService.put(CHANGESET_STORE, REPOSITORY, "42", ciStatus);

      verify(eventBus).post(argThat(
        event -> {
          CIStatusEvent ciStatusEvent = assertInstanceOf(CIStatusEvent.class, event);
          assertThat(ciStatusEvent.getCiStatus()).isSameAs(ciStatus);
          assertThat(ciStatusEvent.getId()).isEqualTo("42");
          assertThat(ciStatusEvent.getRepository()).isEqualTo(REPOSITORY);
          return true;
        }
      ));
    }

    @Nested
    class ForBranch {

      @Mock
      private RepositoryService repositoryService;
      @Mock
      private LogCommandBuilder logCommandBuilder;

      @BeforeEach
      void setUpRepositoryService() {
        lenient().when(repositoryServiceFactory.create(REPOSITORY)).thenReturn(repositoryService);
      }

      @Test
      void shouldGetStatus() throws IOException {
        when(repositoryService.getLogCommand()).thenReturn(logCommandBuilder);
        when(logCommandBuilder.setBranch("develop")).thenReturn(logCommandBuilder);
        when(logCommandBuilder.setPagingLimit(1)).thenReturn(logCommandBuilder);
        when(logCommandBuilder.getChangesets()).thenReturn(new ChangesetPagingResult(1, singletonList(new Changeset("123456", null, null, null))));

        CIStatus ciStatus = new CIStatus("test", "name", null, Status.PENDING, "http://abc.de");
        ciStatusService.put(CHANGESET_STORE, REPOSITORY, "123456", ciStatus);

        CIStatusCollection result = ciStatusService.getByBranch(REPOSITORY, "develop");

        assertThat(result.get("test", "name")).isSameAs(ciStatus);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private class TestingDataStoreFactory implements DataStoreFactory {

    private Map<String, DataStore> stores = new HashMap<>();

    @Override
    public <T> DataStore<T> getStore(TypedStoreParameters<T> storeParameters) {
      return stores.computeIfAbsent(storeParameters.getRepositoryId(), (id) -> new InMemoryDataStore<>());
    }
  }
}
