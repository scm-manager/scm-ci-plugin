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
import com.cloudogu.scm.ci.cistatus.workflow.CIStatusNamedSuccessRule.Configuration;
import com.cloudogu.scm.review.workflow.Context;
import com.cloudogu.scm.review.workflow.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CIStatusNamedSuccessRuleTest {

  @InjectMocks
  private CIStatusNamedSuccessRule rule;

  @Mock
  private Context context;

  @Mock
  private CIStatusResolver statusResolver;

  @Test
  void shouldFailWhenCIStatusCollectionIsEmpty() {
    CIStatusCollection collection = new CIStatusCollection();
    when(statusResolver.resolve(context)).thenReturn(collection);

    Result result = rule.validate(context);
    assertThat(result.isFailed()).isTrue();
  }

  @Test
  void shouldMarshallAndUnmarshallConfiguration() {
    Configuration configuration = new Configuration("jenkins", "build");
    Configuration testedConfiguration = configuration; // TODO: perform test marshalling
    assertThat(testedConfiguration).isNotNull();
    assertThat(testedConfiguration.getName()).isEqualTo(configuration.getName());
    assertThat(testedConfiguration.getType()).isEqualTo(configuration.getType());
  }

  @Test
  void shouldAllowNullAsNameInConfiguration() {
    Configuration configuration = new Configuration("jenkins", null);
    // TODO: validate (expect success)
  }

  @Test
  void shouldNotAllowNullAsTypeInConfiguration() {
    Configuration configuration = new Configuration(null, "build");
    // TODO: validate (expect failure)
  }

  @Nested
  class WithNoNameSpecified {

    @BeforeEach
    void configureRule() {
      when(context.getConfiguration(Configuration.class)).thenReturn(new Configuration("jenkins", null));
    }

    @Test
    void shouldSucceedWhenCIStatusOfTypeIsSuccessful() {
      CIStatusCollection collection = new CIStatusCollection();
      CIStatus status = new CIStatus();
      status.setType("jenkins");
      status.setStatus(Status.SUCCESS);
      collection.put(status);
      when(statusResolver.resolve(context)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldFailWhenTypeMatchesButStatusIsNotSuccess() {
      CIStatusCollection collection = new CIStatusCollection();
      CIStatus status = new CIStatus();
      status.setType("jenkins");
      status.setStatus(Status.FAILURE);
      collection.put(status);
      when(statusResolver.resolve(context)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isFailed()).isTrue();
    }
  }

  @Nested
  class WithNameSpecified {
    @BeforeEach
    void configureRule() {
      when(context.getConfiguration(Configuration.class)).thenReturn(new Configuration("jenkins", "build"));
    }

    @Test
    void shouldSucceedWhenCIStatusOfTypeAndNameIsSuccessful() {
      CIStatusCollection collection = new CIStatusCollection();
      CIStatus status = new CIStatus();
      status.setType("jenkins");
      status.setName("build");
      status.setStatus(Status.SUCCESS);
      collection.put(status);
      when(statusResolver.resolve(context)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldFailWhenNameDoesNotMatch() {
      CIStatusCollection collection = new CIStatusCollection();
      CIStatus status = new CIStatus();
      status.setType("jenkins");
      status.setName("test");
      status.setStatus(Status.FAILURE);
      collection.put(status);
      when(statusResolver.resolve(context)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isFailed()).isTrue();
    }

    @Test
    void shouldFailWhenTypeDoesNotMatch() {
      CIStatusCollection collection = new CIStatusCollection();
      CIStatus status = new CIStatus();
      status.setType("sonarqube");
      status.setName("build");
      status.setStatus(Status.FAILURE);
      collection.put(status);
      when(statusResolver.resolve(context)).thenReturn(collection);

      Result result = rule.validate(context);
      assertThat(result.isFailed()).isTrue();
    }
  }
}
