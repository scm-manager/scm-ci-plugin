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

import React, { FC, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { BranchDetails, Changeset, Repository } from "@scm-manager/ui-types";
import { SmallLoadingSpinner } from "@scm-manager/ui-components";
import { Popover } from "@scm-manager/ui-overlays";
import { CIStatus } from "./CIStatus";
import {
  getStatusVariantForCIStatus,
  getTitleForCIStatus
} from "./CITitle";
import CIStatusList from "./CIStatusList";
import { Card } from "@scm-manager/ui-layout";
import { StatusIcon } from "@scm-manager/ui-core";

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

  const icon = <StatusIcon variant={getStatusVariantForCIStatus(ciStatus)} />;

  const errors =
    ciStatus && ciStatus.length > 0
      ? ciStatus.filter(ci => ci.status === "FAILURE" || ci.status === "UNSTABLE").length
      : 0;

  const trigger = (
    <Card.Details.ButtonDetail
      aria-label={t("scm-ci-plugin.statusbar.aria.label", {
        status: t(`scm-ci-plugin.statusbar.aria.status.${getTitleForCIStatus(ciStatus)}`)
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
