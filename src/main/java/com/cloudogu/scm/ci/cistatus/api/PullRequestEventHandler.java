/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cloudogu.scm.ci.cistatus.api;


import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.ci.cistatus.service.Status;
import com.cloudogu.scm.review.pullrequest.service.PullRequestMergedEvent;
import com.cloudogu.scm.review.pullrequest.service.PullRequestRejectedEvent;
import com.github.legman.Subscribe;
import sonia.scm.plugin.Requires;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Collectors;

import static com.cloudogu.scm.ci.cistatus.service.CIStatusCollection.toCollection;

@Requires("scm-review-plugin")
@Singleton
public class PullRequestEventHandler {

  private final CIStatusService statusService;

  @Inject
  public PullRequestEventHandler(CIStatusService statusService) {
    this.statusService = statusService;
  }

  @Subscribe
  public void handleMergedPRs(PullRequestMergedEvent event) {
    Repository repository = event.getRepository();
    String prId = event.getPullRequest().getId();
    deleteAllUnfinishedCiStatuses(repository, prId);
  }

  @Subscribe
  public void handleRejectedPRs(PullRequestRejectedEvent event) {
    Repository repository = event.getRepository();
    String prId = event.getPullRequest().getId();
    deleteAllUnfinishedCiStatuses(repository, prId);
  }

  private void deleteAllUnfinishedCiStatuses(Repository repository, String prId) {
    CIStatusCollection ciStatuses = statusService.get(CIStatusStore.PULL_REQUEST_STORE, repository, prId);

    statusService.overwriteCollection(
      CIStatusStore.PULL_REQUEST_STORE,
      repository,
      prId,
      getFinishedCiStatuses(ciStatuses));
  }

  private static CIStatusCollection getFinishedCiStatuses(CIStatusCollection ciStatuses) {
    return toCollection(ciStatuses.stream().filter(status -> !status.getStatus().equals(Status.PENDING)).collect(Collectors.toList()));
  }
}
