package com.cloudogu.scm.ci;

import com.cloudogu.scm.ci.cistatus.api.CIStatusRootResource;
import de.otto.edison.hal.HalRepresentation;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import javax.inject.Provider;

import static com.cloudogu.scm.ci.PermissionCheck.mayRead;

@Extension
@Enrich(Changeset.class)
public class ChangeSetLinkEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;

  @Inject
  public ChangeSetLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    Changeset changeset = context.oneRequireByType(Changeset.class);

      if (mayRead(repository)) {
        LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get().get(), CIStatusRootResource.class);
        appender.appendLink("ciStatus", linkBuilder.method("getAll").parameters(repository.getNamespace(), repository.getName(), changeset.getId()).href());
        appender.appendEmbedded("ciStatus", new HalRepresentation());
      }
  }
}

