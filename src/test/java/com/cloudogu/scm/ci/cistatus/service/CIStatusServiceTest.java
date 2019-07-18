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
import static org.mockito.Mockito.when;
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
      when(subject.isPermitted(any(String.class))).thenReturn(false);
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
      assertThrows(UnauthorizedException.class, () -> ciStatusService.put(repository, "1234", new CIStatusCollection()));
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
      when(subject.isPermitted(any(String.class))).thenReturn(true);
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
    void shouldPutAndGetOneRepositoryAndOneChangeSet() {
      Repository repository = createHeartOfGold();
      repository.setId("42");
      CIStatusCollection ciStatusCollection = new CIStatusCollection();

      ciStatusService.put(repository, "123456", ciStatusCollection);

      CIStatusCollection result = ciStatusService.get(repository, "123456");

      assertThat(result).isSameAs(ciStatusCollection);
    }

    @Test
    void shouldPutAndGetOneRepositoryAndTwoChangeSets() {
      Repository repository = createHeartOfGold();
      repository.setId("42");

      CIStatusCollection ciStatusCollection = new CIStatusCollection();
      ciStatusCollection.put(new CIStatus("test", "name", Status.PENDING, "http://abc.de"));

      ciStatusService.put(repository, "123456", ciStatusCollection);
      ciStatusService.put(repository, "654321", ciStatusCollection);

      CIStatusCollection result = ciStatusService.get(repository, "123456");

      assertThat(result).isSameAs(ciStatusCollection);
    }

    @Test
    void shouldPutAndGetTwoRepositoriesAndOneChangeSetEach() {
      Repository repository1 = createHeartOfGold();
      repository1.setId("42");

      Repository repository2 = createRestaurantAtTheEndOfTheUniverse();
      repository2.setId("24");

      CIStatusCollection ciStatusCollection1 = new CIStatusCollection();
      ciStatusCollection1.put(new CIStatus("test", "name", Status.PENDING, "http://abc.de"));

      CIStatusCollection ciStatusCollection2 = new CIStatusCollection();
      ciStatusCollection2.put(new CIStatus("test2", "name2", Status.PENDING, "http://abc.de"));

      ciStatusService.put(repository1, "123456", ciStatusCollection1);
      ciStatusService.put(repository2, "654321", ciStatusCollection2);

      CIStatusCollection resultWithRepo1 = ciStatusService.get(repository1, "123456");
      CIStatusCollection resultWithRepo2 = ciStatusService.get(repository2, "654321");

      assertThat(resultWithRepo1).isSameAs(ciStatusCollection1);
      assertThat(resultWithRepo2).isSameAs(ciStatusCollection2);
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
