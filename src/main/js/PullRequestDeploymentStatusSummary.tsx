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

import React, { FC } from "react";
import { HalRepresentation, Repository } from "@scm-manager/ui-types";
import DeploymentStatusSummary from "./DeploymentStatusSummary";

type Props = {
  repository: Repository;
  pullRequest: { id: string } & HalRepresentation;
};

const PullRequestDeploymentStatusSummary: FC<Props> = ({ repository, pullRequest }) => {
  if (!pullRequest?._links?.["deploymentStatus"]) {
    return null;
  }

  return <DeploymentStatusSummary repository={repository} pullRequest={pullRequest} />;
};

export default PullRequestDeploymentStatusSummary;
