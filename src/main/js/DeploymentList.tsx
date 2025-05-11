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
import { Deployment, useDeleteDeployment } from "./Deployment";
import { OverlayLink } from "./ModalRow";
import { useTranslation } from "react-i18next";
import { SmallLoadingSpinner } from "@scm-manager/ui-components";
import { BranchDetails, Changeset, HalRepresentation, Repository } from "@scm-manager/ui-types";
import { Button, Icon, StatusIcon, StatusVariants } from "@scm-manager/ui-core";

type EntryProps = {
  deployment: Deployment;
  repository: Repository;
  changeset?: Changeset;
  branchDetails?: BranchDetails;
  pullRequest?: { id: string } & HalRepresentation;
};

const DeploymentStatusIcon: FC<Pick<EntryProps, "deployment">> = ({ deployment }) => {
  switch (deployment.status) {
    case "FAILURE":
      return (
        <>
          <StatusIcon variant={StatusVariants.DANGER} iconSize="md" />
        </>
      );
    case "SUCCESS":
      return (
        <>
          <StatusIcon variant={StatusVariants.SUCCESS} iconSize="md" />
        </>
      );
    case "PENDING":
      return (
        <>
          <StatusIcon variant={StatusVariants.IN_PROGRESS} iconSize="md" />
        </>
      );
  }
};

const DeploymentDeleteButton: FC<EntryProps> = ({ deployment, repository, changeset, branchDetails, pullRequest }) => {
  const [t] = useTranslation("plugins");
  const { mutate, isLoading } = useDeleteDeployment({ repository, changeset, branchDetails, pullRequest });

  if (!deployment?._links?.["delete"]) {
    return null;
  }

  return (
    <>
      {isLoading ? (
        <SmallLoadingSpinner className="ml-4" />
      ) : (
        <Button className="ml-4" onClick={() => mutate(deployment)} aria-label={t("scm-ci-plugin.deployment.delete")}>
          <Icon>trash</Icon>
        </Button>
      )}
    </>
  );
};

const DeploymentEntry: FC<EntryProps> = ({ deployment, repository, changeset, branchDetails, pullRequest }) => {
  const [t] = useTranslation("plugins");
  const displayName = deployment.displayName
    ? deployment.displayName
    : t("scm-ci-plugin.deployment.modal.sourceAndEnvironment", {
        source: deployment.source,
        environment: deployment.environment,
      });

  return (
    <>
      <div className="is-flex is-flex-direction-row px-0 py-4 is-flex-grow-1">
        <DeploymentStatusIcon deployment={deployment} />
        <OverlayLink
          href={deployment.url}
          target="_blank"
          rel="noopener noreferrer"
          className="has-hover-background-blue"
        >
          <span className="px-2 has-text-default">{displayName}</span>
        </OverlayLink>
      </div>
      <DeploymentDeleteButton
        deployment={deployment}
        repository={repository}
        changeset={changeset}
        branchDetails={branchDetails}
        pullRequest={pullRequest}
      />
    </>
  );
};

type ListProps = {
  deployments: Deployment[];
  repository: Repository;
  changeset?: Changeset;
  branchDetails?: BranchDetails;
  pullRequest?: { id: string } & HalRepresentation;
};

const DeploymentList: FC<ListProps> = ({ deployments, repository, changeset, branchDetails, pullRequest }) => {
  return (
    <>
      <hr className="mb-0 mt-4" />
      {deployments.map((deployment, index) => (
        <div className="is-flex is-align-items-center" key={`${deployment.source}-${deployment.environment}`}>
          <DeploymentEntry
            deployment={deployment}
            repository={repository}
            changeset={changeset}
            branchDetails={branchDetails}
            pullRequest={pullRequest}
          />
          {index < deployments.length - 1 ? <hr className="m-0" /> : null}
        </div>
      ))}
    </>
  );
};

export default DeploymentList;
