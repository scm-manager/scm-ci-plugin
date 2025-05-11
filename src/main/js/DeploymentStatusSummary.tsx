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
import { useDeployment } from "./Deployment";
import { Card } from "@scm-manager/ui-layout";
import { useTranslation } from "react-i18next";
import { getStatusVariantForDeployments } from "./CITitle";
import { Popover } from "@scm-manager/ui-overlays";
import DeploymentList from "./DeploymentList";
import { BranchDetails, Changeset, HalRepresentation, Repository } from "@scm-manager/ui-types";
import { SmallLoadingSpinner } from "@scm-manager/ui-components";
import { StatusIcon } from "@scm-manager/ui-core";

type Props = {
  repository: Repository;
  changeset?: Changeset;
  branchDetails?: BranchDetails;
  pullRequest?: { id: string } & HalRepresentation;
};

const DeploymentStatusSummary: FC<Props> = ({ repository, changeset, branchDetails, pullRequest }) => {
  const [t] = useTranslation("plugins");
  const { deployments, isLoading } = useDeployment({ repository, changeset, branchDetails, pullRequest });

  if (isLoading) {
    return (
      <Card.Details.Detail>
        <Card.Details.Detail.Label>{t("scm-ci-plugin.deployment.statusbar.title")}</Card.Details.Detail.Label>
        <SmallLoadingSpinner />
      </Card.Details.Detail>
    );
  }

  if (!deployments || deployments.length === 0) {
    return null;
  }

  const trigger = (
    <Card.Details.ButtonDetail aria-label={t("scm-ci-plugin.deployment.statusbar.title")}>
      <Card.Details.Detail.Label>{t("scm-ci-plugin.deployment.statusbar.title")}</Card.Details.Detail.Label>
      <StatusIcon variant={getStatusVariantForDeployments(deployments ?? [])} />
    </Card.Details.ButtonDetail>
  );
  const failedDeploymentsCount = deployments.filter((deployment) => deployment.status === "FAILURE").length;
  const title = (
    <h3 className="has-text-weight-bold is-size-5">
      {t("scm-ci-plugin.deployment.modal.title", { count: failedDeploymentsCount })}
    </h3>
  );

  return (
    <Popover trigger={trigger} title={title}>
      <DeploymentList
        deployments={deployments ?? []}
        repository={repository}
        changeset={changeset}
        branchDetails={branchDetails}
        pullRequest={pullRequest}
      />
    </Popover>
  );
};

export default DeploymentStatusSummary;
