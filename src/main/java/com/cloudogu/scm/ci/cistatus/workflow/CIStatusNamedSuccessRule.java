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
import com.cloudogu.scm.review.workflow.ResultContextWithTranslationCode;
import com.cloudogu.scm.review.workflow.Rule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.Requires;
import sonia.scm.util.GlobUtil;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import java.util.Optional;

@Extension
@Requires("scm-review-plugin")
public class CIStatusNamedSuccessRule implements Rule {

  private final CIStatusResolver statusResolver;

  @Inject
  public CIStatusNamedSuccessRule(CIStatusResolver statusResolver) {
    this.statusResolver = statusResolver;
  }

  @Override
  public boolean isApplicableMultipleTimes() {
    return true;
  }

  @Override
  public Optional<Class<?>> getConfigurationType() {
    return Optional.of(CIStatusNamedSuccessRuleConfiguration.class);
  }

  @Override
  public Result validate(Context context) {
    CIStatusNamedSuccessRuleConfiguration configuration = context.getConfiguration(CIStatusNamedSuccessRuleConfiguration.class);
    CIStatusCollection ciStatuses = statusResolver.resolve(context, configuration.isIgnoreChangesetStatus());
    for (CIStatus status : ciStatuses) {
      if (statusMatchesConfiguration(status, configuration)) {
        return status.getStatus() == Status.SUCCESS ? success() : failed(new CIStatusNamedSuccessRuleErrorContext(configuration.getType(), configuration.getName(), "CiStatusNotSuccessful"));
      }
    }
    return failed(new CIStatusNamedSuccessRuleErrorContext(configuration.getType(), configuration.getName(), "CiStatusMissing"));
  }

  private boolean statusMatchesConfiguration(CIStatus status, CIStatusNamedSuccessRuleConfiguration configuration) {
    return Objects.equals(status.getType(), configuration.getType())
      && GlobUtil.matches(configuration.getName(), status.getName());
  }

  @Getter
  @AllArgsConstructor
  public static class CIStatusNamedSuccessRuleErrorContext implements ResultContextWithTranslationCode {
    private final String type;
    private final String name;
    private final String translationCode;
  }

  @XmlRootElement
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class CIStatusNamedSuccessRuleConfiguration extends BasicConfigration {
    @NotBlank
    private String type;
    @NotBlank
    private String name;
  }
}
