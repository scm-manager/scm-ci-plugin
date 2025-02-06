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
import { Deployment } from "./Deployment";
import { Modal } from "@scm-manager/ui-components";
import { useTranslation } from "react-i18next";
import DeploymentList from "./DeploymentList";
import { HalRepresentation, Repository } from "@scm-manager/ui-types";

type Props = {
  repository: Repository;
  pullRequest: HalRepresentation & { id: string };
  deployments: Deployment[];
  failedDeploymentsCount: number;
  onClose: () => void;
};

const DeploymentStatusModalView: FC<Props> = ({
  repository,
  pullRequest,
  deployments,
  failedDeploymentsCount,
  onClose
}) => {
  const [t] = useTranslation("plugins");
  return (
    <Modal
      title={t("scm-ci-plugin.deployment.modal.title", { count: failedDeploymentsCount })}
      closeFunction={onClose}
      active={true}
      body={<DeploymentList repository={repository} pullRequest={pullRequest} deployments={deployments} />}
    />
  );
};

export default DeploymentStatusModalView;
