package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.ci.cistatus.service.Status;
import de.otto.edison.hal.HalRepresentation;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangesetStatusEnricherTest {

  static final Repository REPOSITORY = new Repository("1", "git", "space", "X");
  static final Changeset CHANGESET = new Changeset("123", System.currentTimeMillis(), null);

  @Mock
  CIStatusService ciStatusService;
  @Mock
  CIStatusMapper mapper;
  @Mock
  CIStatusPathBuilder pathBuilder;

  @InjectMocks
  ChangesetStatusEnricher enricher;

  @Mock
  HalEnricherContext context;
  @Mock
  HalAppender appender;

  @Mock
  Subject subject;

  @BeforeEach
  void setUpDefaults() {
    ThreadContext.bind(subject);
    lenient().when(context.oneRequireByType(Repository.class)).thenReturn(REPOSITORY);
    lenient().when(context.oneRequireByType(Changeset.class)).thenReturn(CHANGESET);
  }

  @Test
  void shouldNotEnrichWithoutPermissions() {
    when(subject.isPermitted(any(String.class))).thenReturn(false);

    enricher.enrich(context, appender);

    verify(appender, never()).appendLink(any(), any());
  }

  @Test
  void shouldAppendLink() {
    when(subject.isPermitted("repository:readCIStatus:1")).thenReturn(true);
    when(ciStatusService.get(REPOSITORY, CHANGESET.getId())).thenReturn(new CIStatusCollection());
    when(pathBuilder.createCollectionUri("space", "X", "123")).thenReturn("http://scm.com");

    enricher.enrich(context, appender);

    verify(appender).appendLink("ciStatus", "http://scm.com");
  }

  @Test
  void shouldEmbedStatus() {
    when(subject.isPermitted("repository:readCIStatus:1")).thenReturn(true);
    CIStatusCollection collection = new CIStatusCollection();
    CIStatus ciStatus = new CIStatus("ci", "status", null, Status.PENDING, "http://ci.com");
    collection.put(ciStatus);
    when(ciStatusService.get(REPOSITORY, CHANGESET.getId())).thenReturn(collection);
    when(pathBuilder.createCollectionUri("space", "X", "123")).thenReturn("http://scm.com");
    CIStatusDto dto = new CIStatusDto();
    when(mapper.map(REPOSITORY, "123", ciStatus)).thenReturn(dto);

    enricher.enrich(context, appender);

    verify(appender).appendEmbedded(eq("ciStatus"), (List<HalRepresentation>) argThat(argument -> {
      assertThat((List) argument).containsExactly(dto);
      return true;
    }));
  }
}
