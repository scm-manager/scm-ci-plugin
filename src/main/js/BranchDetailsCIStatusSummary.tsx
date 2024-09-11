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
import { Branch, Repository } from "@scm-manager/ui-types";
import CIStatusSummary from "./CIStatusSummary";
import { useCiStatus } from "./CIStatus";

type Props = {
  repository: Repository;
  branch: Branch;
};

const BranchDetailsCIStatusSummary: FC<Props> = ({ repository, branch }) => {
  const { data, isLoading } = useCiStatus(repository, { branch });

  return <CIStatusSummary explicitCiStatus={data} repository={repository} loading={isLoading} />;
};

export default BranchDetailsCIStatusSummary;
