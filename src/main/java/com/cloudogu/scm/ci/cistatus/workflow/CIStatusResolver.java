package com.cloudogu.scm.ci.cistatus.workflow;

import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.workflow.Context;
import sonia.scm.repository.Repository;

import javax.inject.Inject;

public class CIStatusResolver {

  private final CIStatusService ciStatusService;
  private final SourceRevisionResolver sourceRevisionResolver;

  @Inject
  public CIStatusResolver(CIStatusService ciStatusService, SourceRevisionResolver sourceRevisionResolver) {
    this.ciStatusService = ciStatusService;
    this.sourceRevisionResolver = sourceRevisionResolver;
  }

  public CIStatusCollection resolve(Context context) {
    Repository repository = context.getRepository();
    PullRequest pullRequest = context.getPullRequest();
    String sourceRevision = sourceRevisionResolver.resolve(repository, pullRequest.getSource());
    return ciStatusService.get(repository, sourceRevision);
  }

}
