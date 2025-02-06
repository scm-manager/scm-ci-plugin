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

import com.cloudogu.scm.ci.deployment.service.DeploymentCollection;
import jakarta.inject.Inject;
import sonia.scm.repository.Repository;

import java.util.Collection;

public class DeploymentCollectionMapper {

  private final DeploymentMapper deploymentMapper;

  @Inject
  public DeploymentCollectionMapper(DeploymentMapper deploymentMapper) {
    this.deploymentMapper = deploymentMapper;
  }

  public Collection<DeploymentDto> map(Repository repository, DeploymentCollection deployments) {
    return deployments
      .stream()
      .map(deployment -> deploymentMapper.map(repository, deployment))
      .toList();
  }
}
