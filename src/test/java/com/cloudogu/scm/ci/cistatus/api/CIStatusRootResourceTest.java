package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CIStatusRootResourceTest {

  @Mock
  private CIStatusService service;

  @Mock
  private CIStatusMapper mapper;

  @Mock
  private RepositoryResolver resolver;

  @Mock
  private RepositoryServiceFactory repositoryServiceFactory;

  @Mock
  private RepositoryService repositoryService;

  @InjectMocks
  private CIStatusRootResource rootResource;

  @Test
  void shouldReturnCiStatusResource() throws IOException {
    Repository repository = RepositoryTestData.createHeartOfGold();
    when(resolver.resolve("hitchhiker", "heart-of-gold")).thenReturn(repository);
    when(repositoryServiceFactory.create(any(Repository.class))).thenReturn(repositoryService);
    when(repositoryService.getLogCommand().getChangeset("42").getId()).thenReturn("42");

    CIStatusResource resource = rootResource.getCIStatusResource("hitchhiker", "heart-of-gold", "42");

    assertThat(resource.getRepository()).isSameAs(repository);
    assertThat(resource.getChangesetId()).isEqualTo("42");
  }

}
