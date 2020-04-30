package com.cloudogu.scm.ci.cistatus.workflow;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.Status;
import com.cloudogu.scm.review.workflow.Context;
import com.cloudogu.scm.review.workflow.Result;
import com.cloudogu.scm.review.workflow.Rule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.Requires;

import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Optional;

@Extension
@Requires("scm-review-plugin")
public class CIStatusXSuccessRule implements Rule {

  private final CIStatusResolver statusResolver;

  @Inject
  public CIStatusXSuccessRule(CIStatusResolver statusResolver) {
    this.statusResolver = statusResolver;
  }

  @Override
  public Result validate(Context context) {
    Configuration configuration = context.getConfiguration(Configuration.class);
    long successfulCount = countSuccessful(context);
    if (successfulCount >= configuration.getNumberOfSuccessful()) {
      return success();
    }
    return failed(new FailedContext(configuration.getNumberOfSuccessful(), successfulCount));
  }

  private long countSuccessful(Context context) {
    return statusResolver.resolve(context)
      .stream()
      .map(CIStatus::getStatus)
      .filter(s -> s == Status.SUCCESS)
      .count();
  }

  @Getter
  @AllArgsConstructor
  public static class FailedContext {
    private final int expected;
    private final long current;
  }

  @Override
  public Optional<Class<?>> getConfigurationType() {
    return Optional.of(Configuration.class);
  }

  @Getter
  @XmlRootElement
  @NoArgsConstructor
  @AllArgsConstructor
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Configuration {
    @Min(1)
    private int numberOfSuccessful;
  }
}
