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
import com.cloudogu.scm.ci.cistatus.workflow.CIStatusNamedSuccessRule.CIStatusNamedSuccessRuleConfiguration;
import com.cloudogu.scm.ci.cistatus.workflow.CIStatusNamedSuccessRule.CIStatusNamedSuccessRuleErrorContext;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static sonia.scm.store.SerializationTestUtil.toAndFromJsonAndXml;
import static sonia.scm.web.api.DtoValidator.validate;

@ExtendWith(MockitoExtension.class)
class CIStatusNamedSuccessRuleTest {

  @InjectMocks
  private CIStatusNamedSuccessRule rule;

  @Mock
  private Context context;

  @Mock
  private CIStatusResolver statusResolver;

  @Test
  void shouldMarshallAndUnmarshallConfiguration() throws JsonProcessingException {
    CIStatusNamedSuccessRuleConfiguration configuration = new CIStatusNamedSuccessRuleConfiguration("jenkins", "build");
    CIStatusNamedSuccessRuleConfiguration testedConfiguration = toAndFromJsonAndXml(CIStatusNamedSuccessRuleConfiguration.class, configuration);

    assertThat(testedConfiguration).isNotNull();
    assertThat(testedConfiguration.getName()).isEqualTo(configuration.getName());
    assertThat(testedConfiguration.getType()).isEqualTo(configuration.getType());
  }

  @Test
  void shouldNotNullsInTypeConfiguration() {
    CIStatusNamedSuccessRuleConfiguration configuration = new CIStatusNamedSuccessRuleConfiguration(null, "build");

    assertThrows(ConstraintViolationException.class, () -> validate(configuration));
  }

  @Test
  void shouldNotNullsInNameConfiguration() {
    CIStatusNamedSuccessRuleConfiguration configuration = new CIStatusNamedSuccessRuleConfiguration("Jenkins", null);

    assertThrows(ConstraintViolationException.class, () -> validate(configuration));
  }

  @Test
  void shouldNotAllowEmptyStringsInTypeConfiguration() {
    CIStatusNamedSuccessRuleConfiguration configuration = new CIStatusNamedSuccessRuleConfiguration("   ", "build");

    assertThrows(ConstraintViolationException.class, () -> validate(configuration));
  }

  @Test
  void shouldNotAllowEmptyStringsInNameConfiguration() {
    CIStatusNamedSuccessRuleConfiguration configuration = new CIStatusNamedSuccessRuleConfiguration("Jenkins", "   ");

    assertThrows(ConstraintViolationException.class, () -> validate(configuration));
  }

  @Nested
  class TestValidation {
    @BeforeEach
    void configureRule() {
      when(context.getConfiguration(CIStatusNamedSuccessRuleConfiguration.class)).thenReturn(new CIStatusNamedSuccessRuleConfiguration("jenkins", "build"));
    }

    @Test
    void shouldFailWhenCIStatusCollectionIsEmpty() {
      CIStatusCollection collection = new CIStatusCollection();
      when(statusResolver.resolve(context, false)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isFailed()).isTrue();
      assertThat(result.getContext()).isInstanceOf(CIStatusNamedSuccessRuleErrorContext.class);
      CIStatusNamedSuccessRuleErrorContext errorContext = (CIStatusNamedSuccessRuleErrorContext) result.getContext();
      assertThat(errorContext.getType()).isEqualTo("jenkins");
      assertThat(errorContext.getName()).isEqualTo("build");
      assertThat(errorContext.getTranslationCode()).isEqualTo("CiStatusMissing");
    }

    @Test
    void shouldSucceedWhenCIStatusOfTypeAndNameIsSuccessful() {
      CIStatusCollection collection = new CIStatusCollection();
      CIStatus status = new CIStatus();
      status.setType("jenkins");
      status.setName("build");
      status.setStatus(Status.SUCCESS);
      collection.put(status);
      when(statusResolver.resolve(context, false)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldFailWhenCIStatusOfTypeAndNameIsNotSuccess() {
      CIStatusCollection collection = new CIStatusCollection();

      CIStatus status = new CIStatus();
      status.setType("jenkins");
      status.setName("build");
      status.setStatus(Status.PENDING);
      collection.put(status);

      when(statusResolver.resolve(context, false)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isFailed()).isTrue();
      assertThat(result.getContext()).isInstanceOf(CIStatusNamedSuccessRuleErrorContext.class);
      CIStatusNamedSuccessRuleErrorContext errorContext = (CIStatusNamedSuccessRuleErrorContext) result.getContext();
      assertThat(errorContext.getTranslationCode()).isEqualTo("CiStatusNotSuccessful");
      assertThat(errorContext.getType()).isEqualTo("jenkins");
      assertThat(errorContext.getName()).isEqualTo("build");

    }

    @Test
    void shouldFailWhenNameDoesNotMatch() {
      CIStatusCollection collection = new CIStatusCollection();

      CIStatus status = new CIStatus();
      status.setType("jenkins");
      status.setName("test");
      status.setStatus(Status.FAILURE);
      collection.put(status);

      when(statusResolver.resolve(context, false)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isFailed()).isTrue();
      assertThat(result.getContext()).isInstanceOf(CIStatusNamedSuccessRuleErrorContext.class);
      CIStatusNamedSuccessRuleErrorContext errorContext = (CIStatusNamedSuccessRuleErrorContext) result.getContext();
      assertThat(errorContext.getTranslationCode()).isEqualTo("CiStatusMissing");
      assertThat(errorContext.getType()).isEqualTo("jenkins");
      assertThat(errorContext.getName()).isEqualTo("build");
    }

    @Test
    void shouldFailWhenTypeDoesNotMatch() {
      CIStatusCollection collection = new CIStatusCollection();

      CIStatus status = new CIStatus();
      status.setType("sonarqube");
      status.setName("build");
      status.setStatus(Status.FAILURE);
      collection.put(status);

      when(statusResolver.resolve(context, false)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isFailed()).isTrue();
      assertThat(result.getContext()).isInstanceOf(CIStatusNamedSuccessRuleErrorContext.class);
      CIStatusNamedSuccessRuleErrorContext errorContext = (CIStatusNamedSuccessRuleErrorContext) result.getContext();
      assertThat(errorContext.getTranslationCode()).isEqualTo("CiStatusMissing");
      assertThat(errorContext.getType()).isEqualTo("jenkins");
      assertThat(errorContext.getName()).isEqualTo("build");
    }
  }

  @Test
  void shouldHeedIgnoreChangesetStatusConfiguration() {
    CIStatusNamedSuccessRuleConfiguration configuration = mock(CIStatusNamedSuccessRuleConfiguration.class);
    when(configuration.isIgnoreChangesetStatus()).thenReturn(true);
    when(context.getConfiguration(CIStatusNamedSuccessRuleConfiguration.class)).thenReturn(configuration);

    when(statusResolver.resolve(context, true)).thenReturn(new CIStatusCollection());

    Result result = rule.validate(context);
    assertThat(result.isFailed()).isTrue();
  }

  @Test
  void shouldCheckWithGlobPattern() {
    when(context.getConfiguration(CIStatusNamedSuccessRuleConfiguration.class)).thenReturn(new CIStatusNamedSuccessRuleConfiguration("jenkins", "* repo *"));
    CIStatusCollection collection = new CIStatusCollection();
    CIStatus status = new CIStatus();
    status.setType("jenkins");
    status.setName("orga » repo » feature/branch");
    status.setStatus(Status.SUCCESS);
    collection.put(status);
    when(statusResolver.resolve(context, false)).thenReturn(collection);

    Result result = rule.validate(context);

    assertThat(result.isSuccess()).isTrue();
  }
}
