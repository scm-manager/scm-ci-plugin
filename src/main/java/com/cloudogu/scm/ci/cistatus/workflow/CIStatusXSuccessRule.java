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
import com.cloudogu.scm.ci.cistatus.service.Status;
import com.cloudogu.scm.review.workflow.Context;
import com.cloudogu.scm.review.workflow.Result;
import com.cloudogu.scm.review.workflow.Rule;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Min;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.Requires;

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
    long successfulCount = countSuccessful(context, configuration);
    if (successfulCount >= configuration.getNumberOfSuccessful()) {
      return success();
    }
    return failed(new FailedContext(configuration.getNumberOfSuccessful(), successfulCount));
  }

  private long countSuccessful(Context context, Configuration configuration) {
    return statusResolver.resolve(context, configuration.isIgnoreChangesetStatus())
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
  public static class Configuration extends BasicConfigration {
    @Min(1)
    private int numberOfSuccessful;
  }
}
