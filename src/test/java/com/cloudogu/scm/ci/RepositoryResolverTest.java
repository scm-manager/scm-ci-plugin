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

import static org.junit.jupiter.api.Assertions.*;
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
