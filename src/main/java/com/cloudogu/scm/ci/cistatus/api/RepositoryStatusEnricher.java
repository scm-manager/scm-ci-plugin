package com.cloudogu.scm.ci.cistatus.api;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;

import javax.inject.Inject;

import static com.cloudogu.scm.ci.PermissionCheck.mayRead;

@Extension
@Enrich(Repository.class)
public class RepositoryStatusEnricher implements HalEnricher {

  private final CIStatusPathBuilder pathBuilder;

  @Inject
  public RepositoryStatusEnricher(CIStatusPathBuilder pathBuilder) {
    this.pathBuilder = pathBuilder;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);

    if (mayRead(repository)) {
      appender.appendLink("ciStatus", pathBuilder.createCollectionUri(repository.getNamespace(), repository.getName(), "REVISION").replace("REVISION", "{revision}"));
    }
  }
}

