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

package com.cloudogu.scm.ci.deployment;

import com.cloudogu.scm.ci.deployment.service.Deployment;
import com.cloudogu.scm.ci.deployment.service.DeploymentStatus;
import com.cloudogu.scm.ci.deployment.service.DeploymentType;

public class DeploymentTestData {

  public static Deployment buildDefaultDeployment(DeploymentType type) {
    return buildDefaultDeployment(type, "1337");
  }

  public static Deployment buildDefaultDeployment(DeploymentType type, String references) {
    Deployment deployment = new Deployment();
    deployment.setType(type);
    deployment.setReferences(references);
    deployment.setSource("ArgoCD");
    deployment.setEnvironment("ARM64+Linux");
    deployment.setDisplayName("Test Deployment");
    deployment.setUrl("https://next-scm.cloudogu.com");
    deployment.setStatus(DeploymentStatus.SUCCESS);

    return deployment;
  }

  public static Deployment buildDefaultDeployment(DeploymentType type, String source, String environment) {
    Deployment deployment = buildDefaultDeployment(type, "1337");
    deployment.setSource(source);
    deployment.setEnvironment(environment);
    return deployment;
  }
}
