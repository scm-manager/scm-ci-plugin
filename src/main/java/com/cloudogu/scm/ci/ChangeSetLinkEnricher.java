package com.cloudogu.scm.ci;

import com.cloudogu.scm.ci.cistatus.api.CIStatusDtoCollection;
import com.cloudogu.scm.ci.cistatus.api.CIStatusMapper;
import com.cloudogu.scm.ci.cistatus.api.CIStatusResource;
import com.cloudogu.scm.ci.cistatus.api.CIStatusRootResource;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
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

import java.util.stream.Collectors;

import static com.cloudogu.scm.ci.PermissionCheck.mayRead;

@Extension
@Enrich(Changeset.class)
public class ChangeSetLinkEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;
  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;

  @Inject
  public ChangeSetLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore, CIStatusService ciStatusService, CIStatusMapper mapper) {
    this.scmPathInfoStore = scmPathInfoStore;
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    Changeset changeset = context.oneRequireByType(Changeset.class);

      if (mayRead(repository)) {
        LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get().get(), CIStatusRootResource.class, CIStatusResource.class);
        appender.appendLink("ciStatus", linkBuilder.method("getCIStatusResource").parameters(repository.getNamespace(), repository.getName(), changeset.getId()).method("getAll").parameters().href());
        appender.appendEmbedded("ciStatus",
          new CIStatusDtoCollection(
            ciStatusService.get(repository, changeset.getId())
              .stream()
              .map(ciStatus -> mapper.map(repository, changeset.getId(), ciStatus))
              .collect(Collectors.toList())));
      }
  }
}

