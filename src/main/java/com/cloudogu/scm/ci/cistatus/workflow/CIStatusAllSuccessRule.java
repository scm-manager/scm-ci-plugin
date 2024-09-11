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
import com.cloudogu.scm.review.workflow.Rule;
import jakarta.inject.Inject;
import jakarta.xml.bind.annotation.XmlRootElement;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.Requires;

import java.util.Optional;


@Extension
@Requires("scm-review-plugin")
public class CIStatusAllSuccessRule implements Rule {

  private final CIStatusResolver statusResolver;

  @Inject
  public CIStatusAllSuccessRule(CIStatusResolver statusResolver) {
    this.statusResolver = statusResolver;
  }

  @Override
  public Optional<Class<?>> getConfigurationType() {
    return Optional.of(Configuration.class);
  }

  @Override
  public Result validate(Context context) {
    Configuration configuration = context.getConfiguration(Configuration.class);
    boolean ignoreChangesetStatus = configuration != null && configuration.isIgnoreChangesetStatus();
    CIStatusCollection ciStatuses = statusResolver.resolve(context, ignoreChangesetStatus);
    for (CIStatus status : ciStatuses) {
      if (status.getStatus() != Status.SUCCESS) {
        return failed();
      }
    }

    return success();
  }

  @XmlRootElement
  public static class Configuration extends BasicConfigration {
  }
}
