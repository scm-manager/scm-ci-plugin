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

import { apiClient } from "@scm-manager/ui-components";
import { Branch, Link, Repository } from "@scm-manager/ui-types";
import { useQuery } from "react-query";

export type CIStatus = {
  type: string;
  name: string;
  displayName?: string;
  status: string;
  url: string;
};

export const getDisplayName = (ciStatus: CIStatus) => (ciStatus.displayName ? ciStatus.displayName : ciStatus.name);

export type CiStatusContext = {
  pullRequest?: any;
  branch?: Branch;
};

export const useCiStatus = (
  repository: Repository,
  context: CiStatusContext,
  callback?: (ciStatus: CIStatus[]) => void
) => {
  let url: string | undefined;
  if (context.pullRequest) {
    url = (context.pullRequest._links.ciStatus as Link)?.href;
  } else if (context.branch) {
    url = (context.branch._links.details as Link)?.href;
  }

  const { error, isLoading, data } = useQuery<CIStatus[], Error, CIStatus[]>(
    [
      "repository",
      repository.namespace,
      repository.name,
      "cistatus",
      context.pullRequest ? "pull-request" : "branch",
      context.pullRequest?.id || context.branch?.name
    ],
    () =>
      apiClient
        .get(url!)
        .then(response => response.json())
        .then(json => json._embedded.ciStatus)
        .then(ciStatus => {
          if (callback) {
            callback(ciStatus);
          }
          return ciStatus;
        }),
    {
      enabled: !!url
    }
  );

  return {
    error,
    isLoading,
    data
  };
};
