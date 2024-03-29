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
import React, { FC, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { BranchDetails, Changeset, Repository } from "@scm-manager/ui-types";
import { SmallLoadingSpinner } from "@scm-manager/ui-components";
import { Popover } from "@scm-manager/ui-overlays";
import { CIStatus } from "./CIStatus";
import StatusIcon, { getColor, getIcon, getTitle } from "./StatusIcon";
import CIStatusList from "./CIStatusList";
import { Card } from "@scm-manager/ui-layout";

type Props = {
  repository: Repository;
  changeset?: Changeset;
  details?: BranchDetails;
  explicitCiStatus?: CIStatus[];
  loading?: boolean;
};

const CIStatusSummary: FC<Props> = ({ changeset, details, explicitCiStatus, loading }) => {
  const [t] = useTranslation("plugins");

  const ciStatus = useMemo(() => {
    if (changeset?._embedded?.ciStatus) {
      return changeset._embedded.ciStatus as CIStatus[];
    } else if (details?._embedded?.ciStatus) {
      return details._embedded.ciStatus as CIStatus[];
    } else if (explicitCiStatus) {
      return explicitCiStatus;
    }
  }, [changeset, details, explicitCiStatus]);

  if (loading) {
    return (
      <Card.Details.Detail>
        <Card.Details.Detail.Label>{t("scm-ci-plugin.statusbar.title")}</Card.Details.Detail.Label>
        <SmallLoadingSpinner />
      </Card.Details.Detail>
    );
  }

  const icon = <StatusIcon icon={getIcon(ciStatus)} color={getColor(ciStatus)} size="lg" />;

  const errors =
    ciStatus && ciStatus.length > 0
      ? ciStatus.filter(ci => ci.status === "FAILURE" || ci.status === "UNSTABLE").length
      : 0;

  const trigger = (
    <Card.Details.ButtonDetail
      aria-label={t("scm-ci-plugin.statusbar.aria.label", {
        status: t(`scm-ci-plugin.statusbar.aria.status.${getTitle(ciStatus)}`)
      })}
    >
      <Card.Details.Detail.Label>{t("scm-ci-plugin.statusbar.title")}</Card.Details.Detail.Label>
      {icon}
    </Card.Details.ButtonDetail>
  );
  const title = <h1 className="has-text-weight-bold is-size-5">{t("scm-ci-plugin.modal.title", { count: errors })}</h1>;

  return (
    <Popover trigger={trigger} title={title}>
      <CIStatusList ciStatus={ciStatus} />
    </Popover>
  );
};

export default CIStatusSummary;
