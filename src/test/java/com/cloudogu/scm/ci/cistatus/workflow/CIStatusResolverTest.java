package com.cloudogu.scm.ci.cistatus.workflow;

import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.workflow.Context;
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
class CIStatusResolverTest {

  @Mock
  private CIStatusService ciStatusService;

  @Mock
  private SourceRevisionResolver sourceRevisionResolver;

  @InjectMocks
  private CIStatusResolver resolver;

  @Mock
  private Context context;

  @Test
  void shouldResolveCiStatus() {
    Repository repository = RepositoryTestData.createHeartOfGold();
    PullRequest pullRequest = new PullRequest();
    pullRequest.setSource("feature/spaceship");

    CIStatusCollection collection = new CIStatusCollection();

    when(sourceRevisionResolver.resolve(repository, "feature/spaceship")).thenReturn("42");
    when(ciStatusService.get(repository, "42")).thenReturn(collection);

    when(context.getRepository()).thenReturn(repository);
    when(context.getPullRequest()).thenReturn(pullRequest);

    CIStatusCollection resolved = resolver.resolve(context);
    assertThat(resolved).isSameAs(collection);
  }

}
