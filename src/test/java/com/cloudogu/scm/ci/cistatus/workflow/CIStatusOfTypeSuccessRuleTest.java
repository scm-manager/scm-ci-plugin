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
import com.cloudogu.scm.ci.cistatus.service.Status;
import com.cloudogu.scm.ci.cistatus.workflow.CIStatusOfTypeSuccessRule.CIStatusOfTypeSuccessRuleConfiguration;
import com.cloudogu.scm.ci.cistatus.workflow.CIStatusOfTypeSuccessRule.CIStatusOfTypeSuccessRuleErrorContext;
import com.cloudogu.scm.review.workflow.Context;
import com.cloudogu.scm.review.workflow.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

      CIStatus status = new CIStatus();
      status.setType("jenkins");
      status.setStatus(Status.SUCCESS);
      collection.put(status);

      CIStatus status2 = new CIStatus();
      status2.setType("jenkins");
      status2.setStatus(Status.SUCCESS);
      collection.put(status2);

      when(statusResolver.resolve(context, false)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldFailWhenAtLeastOneJobIsNotSuccessful() {
      CIStatusCollection collection = new CIStatusCollection();

      CIStatus successStatus = new CIStatus();
      successStatus.setType("jenkins");
      successStatus.setStatus(Status.SUCCESS);
      collection.put(successStatus);

      CIStatus failureStatus = new CIStatus();
      failureStatus.setType("jenkins");
      failureStatus.setDisplayName("build");
      failureStatus.setStatus(Status.FAILURE);
      collection.put(failureStatus);

      CIStatus pendingStatus = new CIStatus();
      pendingStatus.setDisplayName("test");
      pendingStatus.setType("jenkins");
      pendingStatus.setStatus(Status.PENDING);
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
}
