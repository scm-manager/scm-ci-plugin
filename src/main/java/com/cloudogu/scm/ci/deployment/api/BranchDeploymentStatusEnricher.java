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

package com.cloudogu.scm.ci.deployment.api;

import jakarta.inject.Inject;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.BranchDetails;
import sonia.scm.repository.Repository;

import static com.cloudogu.scm.ci.PermissionCheck.mayRead;

@Extension
@Enrich(BranchDetails.class)
public class BranchDeploymentStatusEnricher implements HalEnricher {

  private final DeploymentStatusPathBuilder pathBuilder;

  @Inject
  public BranchDeploymentStatusEnricher(DeploymentStatusPathBuilder pathBuilder) {
    this.pathBuilder = pathBuilder;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    BranchDetails branchDetails = context.oneRequireByType(BranchDetails.class);

    if (!mayRead(repository)) {
      return;
    }

    appender.appendLink(
      "deploymentStatus",
      pathBuilder.createGetBranchDeploymentsLink(repository, branchDetails.getBranchName())
    );
  }
}
