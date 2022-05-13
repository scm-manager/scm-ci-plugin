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

package com.cloudogu.scm.ci.cistatus.workflow;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import com.cloudogu.scm.ci.cistatus.service.Status;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.workflow.Context;
import com.cloudogu.scm.review.workflow.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CIStatusAllSuccessRuleTest {

  @Mock
  private CIStatusResolver statusResolver;

  @InjectMocks
  private CIStatusAllSuccessRule rule;

  @Mock
  private Context context;

  @Mock
  private CIStatusAllSuccessRule.Configuration configuration;

  @BeforeEach
  void initConfiguration() {
    when(context.getConfiguration(CIStatusAllSuccessRule.Configuration.class))
      .thenReturn(configuration);
  }

  @Test
  void shouldReturnSuccessForEmptyCIStatusCollection() {
    CIStatusCollection collection = new CIStatusCollection();
    when(statusResolver.resolve(context, false)).thenReturn(collection);

    Result result = rule.validate(context);
    assertThat(result.isSuccess()).isTrue();
  }

  @Test
  void shouldReturnSuccessForOnlySuccessfulCIStatus() {
    CIStatusCollection collection = new CIStatusCollection();
    collection.put(createStatus(Status.SUCCESS));
    when(statusResolver.resolve(context, false)).thenReturn(collection);

    Result result = rule.validate(context);
    assertThat(result.isSuccess()).isTrue();
  }

  @Test
  void shouldReturnFailureIfOnlyFailedCIStatus() {
    CIStatusCollection collection = new CIStatusCollection();
    collection.put(createStatus(Status.FAILURE));
    when(statusResolver.resolve(context, false)).thenReturn(collection);

    Result result = rule.validate(context);
    assertThat(result.isFailed()).isTrue();
  }

  @Test
  void shouldReturnFailureAtLeastOneFailedCIStatus() {
    CIStatusCollection collection = new CIStatusCollection();
    collection.put(createStatus(Status.SUCCESS));
    collection.put(createStatus(Status.FAILURE));
    when(statusResolver.resolve(context, false)).thenReturn(collection);

    Result result = rule.validate(context);
    assertThat(result.isFailed()).isTrue();
  }

  @Test
  void shouldHeedIgnoreChangesetStatusConfiguration() {
    when(configuration.isIgnoreChangesetStatus()).thenReturn(true);

    CIStatusCollection collection = new CIStatusCollection();
    when(statusResolver.resolve(context, true)).thenReturn(collection);

    Result result = rule.validate(context);
    assertThat(result.isSuccess()).isTrue();
  }

  @Test
  void shouldNotFailWithMissingConfiguration() {
    when(context.getConfiguration(CIStatusAllSuccessRule.Configuration.class))
      .thenReturn(null);
    CIStatusCollection collection = new CIStatusCollection();
    collection.put(createStatus(Status.SUCCESS));
    when(statusResolver.resolve(context, false)).thenReturn(collection);

    Result result = rule.validate(context);

    assertThat(result.isSuccess()).isTrue();
  }

  private CIStatus createStatus(Status status) {
    return new CIStatus("jenkins", "spaceship", "Spaceship", status, "http://hitchhiker.com");
  }

}
