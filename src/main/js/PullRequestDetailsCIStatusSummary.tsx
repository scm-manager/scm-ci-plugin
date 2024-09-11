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
import { useCiStatus } from "./CIStatus";
import CIStatusSummary from "./CIStatusSummary";

type Props = {
  repository: Repository;
  pullRequest: HalRepresentation;
};

const PullRequestDetailsCIStatusSummary: FC<Props> = ({ repository, pullRequest }) => {
  const { data, isLoading } = useCiStatus(repository, { pullRequest });

  return <CIStatusSummary explicitCiStatus={data} repository={repository} loading={isLoading} />;
};

export default PullRequestDetailsCIStatusSummary;
