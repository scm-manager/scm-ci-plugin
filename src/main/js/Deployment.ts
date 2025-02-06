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

import { BranchDetails, Changeset, HalRepresentation, Link, Repository } from "@scm-manager/ui-types";
import { useMutation, useQuery, useQueryClient } from "react-query";
import { apiClient } from "@scm-manager/ui-components";

export type DeploymentStatus = "SUCCESS" | "PENDING" | "FAILURE";

export type Deployment = {
  source: string;
  environment: string;
  displayName?: string;
  url: string;
  status: DeploymentStatus;
} & HalRepresentation;

type DeploymentOrigins = {
  repository: Repository;
  changeset?: Changeset;
  branchDetails?: BranchDetails;
  pullRequest?: { id: string } & HalRepresentation;
};

const deploymentsQueryKey = ({ repository, changeset, branchDetails, pullRequest }: DeploymentOrigins) => {
  const originKeys = [];

  if (changeset) {
    originKeys.push("changeset", changeset.id);
  }

  if (branchDetails) {
    originKeys.push("branch-detail", branchDetails.branchName);
  }

  if (pullRequest) {
    originKeys.push("pull-request", pullRequest.id);
  }

  return ["repository", repository.namespace, repository.name, "deployments", ...originKeys];
};

const deploymentGetLink = ({ changeset, branchDetails, pullRequest }: DeploymentOrigins) => {
  if (changeset) {
    return requiredLink(changeset, "deploymentStatus");
  }

  if (branchDetails) {
    return requiredLink(branchDetails, "deploymentStatus");
  }

  if (pullRequest) {
    return requiredLink(pullRequest, "deploymentStatus");
  }

  throw new Error("No deploymentStatus Link was provided with any origin");
};

export const useDeployment = (deploymentOrigins: DeploymentOrigins) => {
  const { data, isLoading, error } = useQuery<Deployment[], Error>(deploymentsQueryKey(deploymentOrigins), async () => {
    const response = await apiClient.get(deploymentGetLink(deploymentOrigins));
    return await response.json();
  });

  return {
    deployments: data,
    isLoading,
    error
  };
};

export const useDeleteDeployment = (deploymentOrigins: DeploymentOrigins) => {
  const queryClient = useQueryClient();
  const { mutate, isLoading } = useMutation<unknown, Error, Deployment>(
    deployment => apiClient.delete(requiredLink(deployment, "delete")),
    {
      onSuccess: () => {
        return queryClient.invalidateQueries(deploymentsQueryKey(deploymentOrigins));
      }
    }
  );

  return { mutate, isLoading };
};

const requiredLink = (halObject: HalRepresentation, linkName: string): string => {
  if (!halObject._links[linkName]) {
    throw new Error("Could not find link: " + linkName);
  }
  return (halObject._links[linkName] as Link).href;
};
