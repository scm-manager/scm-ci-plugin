package com.cloudogu.scm.ci;

import com.cloudogu.scm.ci.cistatus.api.CIStatusDtoCollection;
import com.cloudogu.scm.ci.cistatus.api.CIStatusResource;
import com.cloudogu.scm.ci.cistatus.api.CIStatusRootResource;
import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import de.otto.edison.hal.Embedded;
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
  private final CIStatusRootResource ciStatusRootResource;

  @Inject
  public ChangeSetLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore, CIStatusRootResource ciStatusRootResource) {
    this.scmPathInfoStore = scmPathInfoStore;
    this.ciStatusRootResource = ciStatusRootResource;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    Changeset changeset = context.oneRequireByType(Changeset.class);

      if (mayRead(repository)) {
        LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get().get(), CIStatusRootResource.class, CIStatusResource.class);
        appender.appendLink("ciStatus", linkBuilder.method("getCIStatusResource").parameters(repository.getNamespace(), repository.getName(), changeset.getId()).method("getAll").parameters().href());
        appender.appendEmbedded("ciStatus", ciStatusRootResource.getCIStatusResource(repository.getNamespace(), repository.getName(), changeset.getId()).getAll());
      }
  }
}

