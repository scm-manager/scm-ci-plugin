package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CIStatusRootResourceTest {

  @Mock
  private CIStatusService service;

  @Mock
  private CIStatusMapper mapper;

  @Mock
  private RepositoryResolver resolver;

  @InjectMocks
  private CIStatusRootResource rootResource;

  @Test
  void shouldReturnCiStatusResource() {
    Repository repository = RepositoryTestData.createHeartOfGold();
    when(resolver.resolve("hitchhiker", "heart-of-gold")).thenReturn(repository);

    CIStatusResource resource = rootResource.getCIStatusResource("hitchhiker", "heart-of-gold", "42");

    assertThat(resource.getRepository()).isSameAs(repository);
    assertThat(resource.getChangesetId()).isEqualTo("42");
  }

}
