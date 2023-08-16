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

import React, { FC } from "react";
import { HalRepresentation, Repository } from "@scm-manager/ui-types";
import { useTranslation } from "react-i18next";
import { Card } from "@scm-manager/ui-layout";
import { useCiStatus } from "./CIStatus";
import CIStatusSummary from "./CIStatusSummary";
import { SmallLoadingSpinner } from "@scm-manager/ui-components";

type Props = {
  repository: Repository;
  pullRequest: HalRepresentation;
};

const PullRequestDetailsCIStatusSummary: FC<Props> = ({ repository, pullRequest }) => {
  const [t] = useTranslation("plugins");
  const { data, isLoading } = useCiStatus(repository, { pullRequest });

  return (
    <Card.Details.Detail>
      {({ labelId }) => (
        <>
          <Card.Details.Detail.Label id={labelId}>{t("scm-ci-plugin.statusbar.title")}</Card.Details.Detail.Label>
          {isLoading ? (
            <SmallLoadingSpinner />
          ) : (
            <CIStatusSummary explicitCiStatus={data} repository={repository} labelId={labelId} />
          )}
        </>
      )}
    </Card.Details.Detail>
  );
};

export default PullRequestDetailsCIStatusSummary;
