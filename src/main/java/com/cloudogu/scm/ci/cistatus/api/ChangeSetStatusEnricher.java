package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import java.util.stream.Collectors;

import static com.cloudogu.scm.ci.PermissionCheck.mayRead;

@Extension
@Enrich(Changeset.class)
public class ChangeSetStatusEnricher implements HalEnricher {

  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;
  private final CIStatusPathBuilder pathBuilder;

  @Inject
  public ChangeSetStatusEnricher(CIStatusService ciStatusService, CIStatusMapper mapper, CIStatusPathBuilder pathBuilder) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.pathBuilder = pathBuilder;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    Changeset changeset = context.oneRequireByType(Changeset.class);

    if (mayRead(repository)) {
      appender.appendLink("ciStatus", pathBuilder.createCollectionUri(repository.getNamespace(), repository.getName(), changeset.getId()));
      appender.appendEmbedded("ciStatus",
        ciStatusService.get(repository, changeset.getId())
          .stream()
          .map(ciStatus -> mapper.map(repository, changeset.getId(), ciStatus))
          .collect(Collectors.toList()));
    }
  }
}

