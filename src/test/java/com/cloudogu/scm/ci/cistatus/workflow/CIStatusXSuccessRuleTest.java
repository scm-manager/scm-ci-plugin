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
