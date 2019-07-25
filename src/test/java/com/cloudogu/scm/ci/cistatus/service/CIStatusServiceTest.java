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
import sonia.scm.repository.Repository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.store.InMemoryDataStore;
import sonia.scm.store.TypedStoreParameters;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static sonia.scm.repository.RepositoryTestData.createHeartOfGold;
import static sonia.scm.repository.RepositoryTestData.createRestaurantAtTheEndOfTheUniverse;

@ExtendWith(MockitoExtension.class)
class CIStatusServiceTest {

  private CIStatusService ciStatusService;

  @Nested
  class WithoutPermission {

    @Mock
    private Subject subject;

    @BeforeEach
    void setUpDataStoreFactory() {
      ciStatusService = new CIStatusService(new TestingDataStoreFactory());
      ThreadContext.bind(subject);
      doThrow(new UnauthorizedException()).when(subject).checkPermission(any(String.class));
    }

    @AfterEach
    void tearDownSubject() {
      ThreadContext.unbindSubject();
    }

    @Test
    void shouldThrowAuthorizationExceptionWhenMissingPermissions() {
      Repository repository = createHeartOfGold();
      repository.setId("42");

      assertThrows(UnauthorizedException.class, () -> ciStatusService.get(repository, "1234"));
      assertThrows(UnauthorizedException.class, () -> ciStatusService.put(repository, "1234", new CIStatus()));
    }
  }

  @Nested
  class WithPermission {

    @Mock
    private Subject subject;

    @BeforeEach
    void setUpDataStoreFactory() {
      ciStatusService = new CIStatusService(new TestingDataStoreFactory());
      ThreadContext.bind(subject);
      lenient().when(subject.isPermitted(any(String.class))).thenReturn(true);
    }

    @AfterEach
    void tearDownSubject() {
      ThreadContext.unbindSubject();
    }

    @Test
    void shouldReturnEmptyCollection() {
      Repository repository = createHeartOfGold();
      repository.setId("42");

      CIStatusCollection ciStatusCollection = ciStatusService.get(repository, "1234");
      assertThat(ciStatusCollection).isNotNull().isEmpty();
    }

    @Test
    void shouldPutAndGetOneRepositoryAndTwoChangeSets() {
      Repository repository = createHeartOfGold();
      repository.setId("42");

      CIStatus ciStatus = new CIStatus("test", "name", null, Status.PENDING, "http://abc.de");
      ciStatusService.put(repository, "123456", ciStatus);
      ciStatusService.put(repository, "654321", ciStatus);

      CIStatusCollection result = ciStatusService.get(repository, "123456");

      assertThat(result.get("test", "name")).isSameAs(ciStatus);
    }

    @Test
    void shouldPutAndGetTwoRepositoriesAndOneChangeSetEach() {
      Repository repository1 = createHeartOfGold();
      repository1.setId("42");

      Repository repository2 = createRestaurantAtTheEndOfTheUniverse();
      repository2.setId("24");

      CIStatus ciStatus1 = new CIStatus("test", "name", null, Status.PENDING, "http://abc.de");
      ciStatusService.put(repository1, "123456", ciStatus1);
      CIStatus ciStatus2 = new CIStatus("test2", "name2", null, Status.PENDING, "http://abc.de");
      ciStatusService.put(repository2, "654321", ciStatus2);

      CIStatusCollection resultWithRepo1 = ciStatusService.get(repository1, "123456");
      CIStatusCollection resultWithRepo2 = ciStatusService.get(repository2, "654321");

      assertThat(resultWithRepo1.get("test", "name")).isSameAs(ciStatus1);
      assertThat(resultWithRepo2.get("test2", "name2")).isSameAs(ciStatus2);
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
