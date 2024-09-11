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
import com.cloudogu.scm.review.workflow.ResultContextWithTranslationCode;
import com.cloudogu.scm.review.workflow.Rule;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.Requires;

import java.util.Objects;
import java.util.Optional;

@Extension
@Requires("scm-review-plugin")
public class CIStatusOfTypeSuccessRule implements Rule {

  private final CIStatusResolver statusResolver;

  @Inject
  public CIStatusOfTypeSuccessRule(CIStatusResolver statusResolver) {
    this.statusResolver = statusResolver;
  }

  @Override
  public boolean isApplicableMultipleTimes() {
    return true;
  }

  @Override
  public Optional<Class<?>> getConfigurationType() {
    return Optional.of(CIStatusOfTypeSuccessRuleConfiguration.class);
  }

  @Override
  public Result validate(Context context) {
    CIStatusOfTypeSuccessRuleConfiguration configuration = context.getConfiguration(CIStatusOfTypeSuccessRuleConfiguration.class);
    CIStatusCollection ciStatuses = statusResolver.resolve(context, configuration.isIgnoreChangesetStatus());
    boolean ciStatusFound = false;
    for (CIStatus status : ciStatuses) {
      if (Objects.equals(status.getType(), configuration.getType())) {
        if (status.getStatus() != Status.SUCCESS) {
          return failed(new CIStatusOfTypeSuccessRuleErrorContext(configuration.getType(), status.getDisplayName(), "CiStatusNotSuccessful"));
        } else {
          ciStatusFound = true;
        }
      }
    }
    return ciStatusFound ? success() : failed(new CIStatusOfTypeSuccessRuleErrorContext(configuration.getType(), null, "CiStatusMissing"));
  }

  @Getter
  @AllArgsConstructor
  public static class CIStatusOfTypeSuccessRuleErrorContext implements ResultContextWithTranslationCode {
    private final String type;
    private final String name;
    private final String translationCode;
  }

  @XmlRootElement
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class CIStatusOfTypeSuccessRuleConfiguration extends BasicConfigration {
    @NotBlank
    private String type;
  }
}
