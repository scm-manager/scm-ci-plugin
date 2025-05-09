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

import React, { FC, useState } from "react";
import { HalRepresentation, Repository } from "@scm-manager/ui-types";
import { ErrorNotification, Loading, StatusIcon } from "@scm-manager/ui-core";
import { useDeployment } from "./Deployment";
import { Notification } from "./StatusBar";
import { Icon } from "@scm-manager/ui-buttons";
import { getStatusVariantForDeployments } from "./CITitle";
import classNames from "classnames";
import { useTranslation } from "react-i18next";
import DeploymentStatusModalView from "./DeploymentStatusModalView";

type Props = {
  repository: Repository;
  pullRequest: HalRepresentation & { id: string };
};

const DeploymentStatusBar: FC<Props> = ({ repository, pullRequest }) => {
  const [t] = useTranslation("plugins");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { error, isLoading, deployments } = useDeployment({ repository, pullRequest });

  if (error) {
    return <ErrorNotification error={error} />;
  }

  if (isLoading) {
    return <Loading />;
  }

  if (!deployments || deployments.length === 0) {
    return null;
  }

  const failedDeploymentsCount = deployments.filter(deployment => deployment.status === "FAILURE").length;

  return (
    <>
      {isModalOpen ? (
        <DeploymentStatusModalView
          repository={repository}
          pullRequest={pullRequest}
          deployments={deployments}
          failedDeploymentsCount={failedDeploymentsCount}
          onClose={() => setIsModalOpen(false)}
        />
      ) : null}
      <Notification className={"media notification is-secondary"} onClick={() => setIsModalOpen(true)} isPointer={true}>
        <StatusIcon variant={getStatusVariantForDeployments(deployments)} className={classNames("is-medium mr-2")}/>
        <span className="has-text-weight-bold">
          {t("scm-ci-plugin.deployment.statusbar.analysis", { count: deployments.length })}
        </span>
        <Icon>angle-right</Icon>
        <span>
          {t("scm-ci-plugin.deployment.statusbar.failed_deployment", {
            count: failedDeploymentsCount
          })}
        </span>
      </Notification>
    </>
  );
};

export default DeploymentStatusBar;
