/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import { apiClient } from "@scm-manager/ui-components";
import { Branch, Link, Repository } from "@scm-manager/ui-types";

/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
  callback: (ciStatus: CIStatus[]) => void
) => {
  const { error, isLoading, data } = useQuery<CIStatus[], Error>(
    [
      "repository",
      repository.namespace,
      repository.name,
      "cistatus",
      context.pullRequest ? "pull-request" : "branch",
      context.pullRequest?.id || context.branch?.name
    ],
    () => {
      if (context.pullRequest) {
        return apiClient
          .get((context.pullRequest._links.ciStatus as Link)?.href)
          .then(response => response.json())
          .then(json => json._embedded.ciStatus)
          .then(ciStatus => {
            callback(ciStatus);
            return ciStatus;
          });
      } else if (context.branch) {
        return apiClient
          .get((context.branch._links.details as Link)?.href)
          .then(response => response.json())
          .then(json => json._embedded.ciStatus)
          .then(ciStatus => {
            callback(ciStatus);
            return ciStatus;
          });
      }
      return [];
    }
  );

  return {
    error,
    isLoading,
    data
  };
};
