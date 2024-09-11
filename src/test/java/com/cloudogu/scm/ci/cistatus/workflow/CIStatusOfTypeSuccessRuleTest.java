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
import com.cloudogu.scm.ci.cistatus.workflow.CIStatusOfTypeSuccessRule.CIStatusOfTypeSuccessRuleConfiguration;
import com.cloudogu.scm.ci.cistatus.workflow.CIStatusOfTypeSuccessRule.CIStatusOfTypeSuccessRuleErrorContext;
import com.cloudogu.scm.review.workflow.Context;
import com.cloudogu.scm.review.workflow.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static sonia.scm.store.SerializationTestUtil.toAndFromJsonAndXml;
import static sonia.scm.web.api.DtoValidator.validate;

@ExtendWith(MockitoExtension.class)
class CIStatusOfTypeSuccessRuleTest {

  @InjectMocks
  private CIStatusOfTypeSuccessRule rule;

  @Mock
  private Context context;

  @Mock
  private CIStatusResolver statusResolver;

  @Test
  void shouldMarshallAndUnmarshallConfiguration() throws JsonProcessingException {
    CIStatusOfTypeSuccessRuleConfiguration configuration = new CIStatusOfTypeSuccessRuleConfiguration("jenkins");
    CIStatusOfTypeSuccessRuleConfiguration testedConfiguration = toAndFromJsonAndXml(CIStatusOfTypeSuccessRuleConfiguration.class, configuration);
    assertThat(testedConfiguration).isNotNull();
    assertThat(testedConfiguration.getType()).isEqualTo(configuration.getType());
  }

  @Test
  void shouldNotNullsInConfiguration() {
    CIStatusOfTypeSuccessRuleConfiguration configuration = new CIStatusOfTypeSuccessRuleConfiguration(null);

    assertThrows(ConstraintViolationException.class, () -> validate(configuration));
  }

  @Test
  void shouldNotAllowEmptyStringsInConfiguration() {
    CIStatusOfTypeSuccessRuleConfiguration configuration = new CIStatusOfTypeSuccessRuleConfiguration("      ");

    assertThrows(ConstraintViolationException.class, () -> validate(configuration));
  }

  @Nested
  class TestValidation {

    @BeforeEach
    void configureRule() {
      when(context.getConfiguration(CIStatusOfTypeSuccessRuleConfiguration.class)).thenReturn(new CIStatusOfTypeSuccessRuleConfiguration("jenkins"));
    }

    @Test
    void shouldFailWhenCIStatusCollectionIsEmpty() {
      CIStatusCollection collection = new CIStatusCollection();
      when(statusResolver.resolve(context, false)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isFailed()).isTrue();
      assertThat(result.getContext()).isInstanceOf(CIStatusOfTypeSuccessRuleErrorContext.class);
      CIStatusOfTypeSuccessRuleErrorContext errorContext = (CIStatusOfTypeSuccessRuleErrorContext) result.getContext();
      assertThat(errorContext.getType()).isEqualTo("jenkins");
      assertThat(errorContext.getName()).isNull();
      assertThat(errorContext.getTranslationCode()).isEqualTo("CiStatusMissing");
    }

    @Test
    void shouldSucceedWhenAllCIStatusOfTypeAreSuccessful() {
      CIStatusCollection collection = new CIStatusCollection();

      CIStatus status = new CIStatus("jenkins", null, null, Status.SUCCESS);
      collection.put(status);

      CIStatus status2 = new CIStatus("jenkins", null, null, Status.SUCCESS);
      collection.put(status2);

      when(statusResolver.resolve(context, false)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldFailWhenAtLeastOneJobIsNotSuccessful() {
      CIStatusCollection collection = new CIStatusCollection();

      CIStatus successStatus = new CIStatus("jenkins", null, null, Status.SUCCESS);
      collection.put(successStatus);

      CIStatus failureStatus = new CIStatus("jenkins", null, "build", Status.FAILURE);
      collection.put(failureStatus);

      CIStatus pendingStatus = new CIStatus("jenkins", null, "test", Status.PENDING);
      collection.put(pendingStatus);

      when(statusResolver.resolve(context, false)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isFailed()).isTrue();
      assertThat(result.getContext()).isInstanceOf(CIStatusOfTypeSuccessRuleErrorContext.class);
      CIStatusOfTypeSuccessRuleErrorContext errorContext = (CIStatusOfTypeSuccessRuleErrorContext) result.getContext();
      assertThat(errorContext.getType()).isEqualTo("jenkins");
      assertTrue(errorContext.getName().equals("test") || errorContext.getName().equals("build"));
      assertThat(errorContext.getTranslationCode()).isEqualTo("CiStatusNotSuccessful");
    }
  }

  @Test
  void shouldHeedIgnoreChangesetStatusConfiguration() {
    CIStatusOfTypeSuccessRuleConfiguration configuration = mock(CIStatusOfTypeSuccessRuleConfiguration.class);
    when(configuration.isIgnoreChangesetStatus()).thenReturn(true);
    when(context.getConfiguration(CIStatusOfTypeSuccessRuleConfiguration.class)).thenReturn(configuration);

    when(statusResolver.resolve(context, true)).thenReturn(new CIStatusCollection());

    Result result = rule.validate(context);
    assertThat(result.isFailed()).isTrue();
  }
}
