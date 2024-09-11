/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.cloudogu.scm.ci.cistatus.workflow;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.Status;
import com.cloudogu.scm.review.workflow.Context;
import com.cloudogu.scm.review.workflow.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
