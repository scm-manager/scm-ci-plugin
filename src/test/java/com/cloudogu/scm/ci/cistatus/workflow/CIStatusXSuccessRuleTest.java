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
import com.cloudogu.scm.review.workflow.Context;
import com.cloudogu.scm.review.workflow.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CIStatusXSuccessRuleTest {

  @Mock
  private CIStatusResolver statusResolver;

  @Mock
  private Context context;

  @InjectMocks
  private CIStatusXSuccessRule rule;

  @Test
  void shouldReturnSuccessForOneConfigured() {
    CIStatusXSuccessRule.Configuration configuration = new CIStatusXSuccessRule.Configuration(1);
    when(context.getConfiguration(CIStatusXSuccessRule.Configuration.class)).thenReturn(configuration);
    CIStatusCollection collection = new CIStatusCollection();
    collection.put(createStatus(Status.SUCCESS));
    when(statusResolver.resolve(context)).thenReturn(collection);

    Result result = rule.validate(context);
    assertThat(result.isSuccess()).isTrue();
  }

  @Test
  void shouldReturnSuccessForMultipleConfigured() {
    CIStatusXSuccessRule.Configuration configuration = new CIStatusXSuccessRule.Configuration(2);
    when(context.getConfiguration(CIStatusXSuccessRule.Configuration.class)).thenReturn(configuration);
    CIStatusCollection collection = new CIStatusCollection();
    collection.put(createStatus("heartOfGold", Status.FAILURE));
    collection.put(createStatus("magratea", Status.SUCCESS));
    collection.put(createStatus("deepThroat", Status.SUCCESS));
    when(statusResolver.resolve(context)).thenReturn(collection);

    Result result = rule.validate(context);
    assertThat(result.isSuccess()).isTrue();
  }

  @Test
  void shouldReturnFailedForOneConfigured() {
    CIStatusXSuccessRule.Configuration configuration = new CIStatusXSuccessRule.Configuration(1);
    when(context.getConfiguration(CIStatusXSuccessRule.Configuration.class)).thenReturn(configuration);
    CIStatusCollection collection = new CIStatusCollection();
    when(statusResolver.resolve(context)).thenReturn(collection);

    Result result = rule.validate(context);
    assertThat(result.isFailed()).isTrue();
    CIStatusXSuccessRule.FailedContext failedContext = (CIStatusXSuccessRule.FailedContext) result.getContext();
    assertThat(failedContext.getExpected()).isEqualTo(1);
    assertThat(failedContext.getCurrent()).isEqualTo(0);
  }

  private CIStatus createStatus(Status status) {
    return createStatus("spaceship", status);
  }

  private CIStatus createStatus(String name, Status status) {
    return new CIStatus("jenkins", name, "Spaceship", status, "http://hitchhiker.com");
  }

  @Test
  void shouldMarshallAndUnmarshallConfiguration() {
    CIStatusXSuccessRule.Configuration configuration = new CIStatusXSuccessRule.Configuration(5);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JAXB.marshal(configuration, output);
    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
    configuration = JAXB.unmarshal(input, CIStatusXSuccessRule.Configuration.class);

    assertThat(configuration.getNumberOfSuccessful()).isEqualTo(5);
  }

}
