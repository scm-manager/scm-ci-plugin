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

import com.cloudogu.scm.ci.deployment.service.DeploymentStatus;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class DeploymentDto extends HalRepresentation {

  @NotEmpty
  private String source;
  @NotEmpty
  private String environment;
  private String displayName;

  private Instant deployedAt;
  @NotEmpty
  //URL should start with either http://, https:// and so on
  @Pattern(regexp = "^(http|https|ftp|ftps|sftp)://.*$")
  private String url;
  @NotNull
  private DeploymentStatus status;

  public DeploymentDto(Links links) {
    super(links);
  }
}
