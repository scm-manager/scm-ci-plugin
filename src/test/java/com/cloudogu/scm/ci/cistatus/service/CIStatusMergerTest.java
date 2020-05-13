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

package com.cloudogu.scm.ci.cistatus.service;

import com.cloudogu.scm.ci.cistatus.workflow.SourceRevisionResolver;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.pullrequest.service.PullRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import static com.cloudogu.scm.ci.cistatus.Constants.CHANGESET_STORE_NAME;
import static com.cloudogu.scm.ci.cistatus.Constants.PULL_REQUEST_STORE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CIStatusMergerTest {

  private static final Repository REPOSITORY = RepositoryTestData.createHeartOfGold();

  @Mock
  private CIStatusService ciStatusService;

  @Mock
  private SourceRevisionResolver sourceRevisionResolver;

  @Mock
  private PullRequestService pullRequestService;

  @InjectMocks
  private CIStatusMerger merger;

  @Test
  void shouldReturnOnlyPullRequestCIStatusesIfBothCIStatusAvailable() {
    String changesetId = "1a2b3c4d";
    PullRequest pullRequest = new PullRequest();
    pullRequest.setId("1");
    pullRequest.setSource("develop");

    String statusName = "JENKINS";
    CIStatus prCIStatus = new CIStatus(statusName, statusName, statusName, Status.SUCCESS, "");
    CIStatusCollection prCIStatusCollection = new CIStatusCollection();
    prCIStatusCollection.put(prCIStatus);

    CIStatus changesetCIStatus = new CIStatus(statusName, statusName, statusName, Status.FAILURE, "");
    CIStatusCollection changesetCIStatusCollection = new CIStatusCollection();
    changesetCIStatusCollection.put(changesetCIStatus);

    when(pullRequestService.get(REPOSITORY, pullRequest.getId())).thenReturn(pullRequest);
    when(sourceRevisionResolver.resolve(REPOSITORY, pullRequest.getSource())).thenReturn(changesetId);
    when(ciStatusService.get(CHANGESET_STORE_NAME, REPOSITORY, changesetId)).thenReturn(changesetCIStatusCollection);
    when(ciStatusService.get(PULL_REQUEST_STORE_NAME, REPOSITORY, pullRequest.getId())).thenReturn(prCIStatusCollection);

    CIStatusCollection result = merger.mergePullRequestCIStatuses(REPOSITORY, pullRequest.getId());

    assertThat((int) result.stream().count()).isEqualTo(1);
    assertThat(result.get(statusName, statusName).getStatus()).isEqualTo(Status.SUCCESS);
  }

  @Test
  void shouldMergeChangesetStatusesToPullRequestStatuses() {
    String changesetId = "1a2b3c4d";
    PullRequest pullRequest = new PullRequest();
    pullRequest.setId("1");
    pullRequest.setSource("develop");

    String status1 = "JENKINS";
    CIStatus prCIStatus = new CIStatus(status1, status1, status1, Status.SUCCESS, "");
    CIStatusCollection prCIStatusCollection = new CIStatusCollection();
    prCIStatusCollection.put(prCIStatus);


    String status2 = "TEAMSCALE";
    CIStatus changesetCIStatus = new CIStatus(status2, status2, status2, Status.FAILURE, "");
    CIStatusCollection changesetCIStatusCollection = new CIStatusCollection();
    changesetCIStatusCollection.put(changesetCIStatus);

    when(pullRequestService.get(REPOSITORY, pullRequest.getId())).thenReturn(pullRequest);
    when(sourceRevisionResolver.resolve(REPOSITORY, pullRequest.getSource())).thenReturn(changesetId);
    when(ciStatusService.get(CHANGESET_STORE_NAME, REPOSITORY, changesetId)).thenReturn(changesetCIStatusCollection);
    when(ciStatusService.get(PULL_REQUEST_STORE_NAME, REPOSITORY, pullRequest.getId())).thenReturn(prCIStatusCollection);

    CIStatusCollection result = merger.mergePullRequestCIStatuses(REPOSITORY, pullRequest.getId());

    assertThat((int) result.stream().count()).isEqualTo(2);
    assertThat(result.get(status1, status1).getStatus()).isEqualTo(Status.SUCCESS);
    assertThat(result.get(status2, status2).getStatus()).isEqualTo(Status.FAILURE);
  }

  @Test
  void shouldMergeChangesetStatusesToPullRequestStatusesIfSameTypeButDifferentName() {
    String changesetId = "1a2b3c4d";
    PullRequest pullRequest = new PullRequest();
    pullRequest.setId("1");
    pullRequest.setSource("develop");

    String statusType = "JENKINS";
    String statusName1 = "JENKINS";
    CIStatus prCIStatus = new CIStatus(statusType, statusName1, statusName1, Status.SUCCESS, "");
    CIStatusCollection prCIStatusCollection = new CIStatusCollection();
    prCIStatusCollection.put(prCIStatus);

    String statusName2 = "TEAMSCALE";
    CIStatus changesetCIStatus = new CIStatus(statusType, statusName2, statusName2, Status.FAILURE, "");
    CIStatusCollection changesetCIStatusCollection = new CIStatusCollection();
    changesetCIStatusCollection.put(changesetCIStatus);

    when(pullRequestService.get(REPOSITORY, pullRequest.getId())).thenReturn(pullRequest);
    when(sourceRevisionResolver.resolve(REPOSITORY, pullRequest.getSource())).thenReturn(changesetId);
    when(ciStatusService.get(CHANGESET_STORE_NAME, REPOSITORY, changesetId)).thenReturn(changesetCIStatusCollection);
    when(ciStatusService.get(PULL_REQUEST_STORE_NAME, REPOSITORY, pullRequest.getId())).thenReturn(prCIStatusCollection);

    CIStatusCollection result = merger.mergePullRequestCIStatuses(REPOSITORY, pullRequest.getId());

    assertThat((int) result.stream().count()).isEqualTo(2);
    assertThat(result.get(statusType, statusName1).getStatus()).isEqualTo(Status.SUCCESS);
    assertThat(result.get(statusType, statusName2).getStatus()).isEqualTo(Status.FAILURE);
  }

}
