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

import com.cloudogu.scm.ci.deployment.service.Deployment;
import com.cloudogu.scm.ci.deployment.service.DeploymentType;
import com.google.common.annotations.VisibleForTesting;
import de.otto.edison.hal.Links;
import jakarta.inject.Inject;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import sonia.scm.repository.Repository;

import static com.cloudogu.scm.ci.PermissionCheck.mayWrite;
import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Links.linkingTo;

@Mapper
public abstract class DeploymentMapper {

  @Inject
  private DeploymentStatusPathBuilder pathBuilder;

  @VisibleForTesting
  void setPathBuilder(DeploymentStatusPathBuilder pathBuilder) {
    this.pathBuilder = pathBuilder;
  }

  @Mapping(target = "attributes", ignore = true)
  public abstract DeploymentDto map(@Context Repository repository, Deployment deployment);

  //Type and references will be set in the createDto object factory
  @Mapping(target = "type", ignore = true)
  @Mapping(target = "references", ignore = true)
  public abstract Deployment map(@Context DeploymentType type, @Context String references, DeploymentDto deploymentDto);

  @ObjectFactory
  Deployment createModel(@Context DeploymentType type, @Context String references) {
    Deployment deployment = new Deployment();
    deployment.setType(type);
    deployment.setReferences(references);
    return deployment;
  }

  @ObjectFactory
  DeploymentDto createDto(@Context Repository repository, Deployment deployment) {
    if (!mayWrite(repository)) {
      return new DeploymentDto();
    }

    Links.Builder linksBuilder = linkingTo()
      .single(link(
        "delete",
        buildDeleteLink(repository, deployment)
      ))
      .single(link(
        "update",
        buildUpdateLink(repository, deployment)
      ));

    return new DeploymentDto(linksBuilder.build());
  }

  private String buildDeleteLink(Repository repository, Deployment deployment) {
    if (deployment.getType() == DeploymentType.PULL_REQUEST) {
      return pathBuilder.createDeletePullRequestDeploymentLink(repository, deployment.getReferences(), deployment);
    } else {
      return pathBuilder.createDeleteChangesetDeploymentLink(repository, deployment.getReferences(), deployment);
    }
  }

  private String buildUpdateLink(Repository repository, Deployment deployment) {
    if (deployment.getType() == DeploymentType.PULL_REQUEST) {
      return pathBuilder.createUpdatePullRequestDeploymentStatusLink(repository, deployment.getReferences());
    } else {
      return pathBuilder.createUpdateChangesetDeploymentStatusLink(repository, deployment.getReferences());
    }
  }
}
