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
import { CIStatus } from "./CIStatus";
import { Deployment } from "./Deployment";
import { StatusIcon, StatusVariants } from "@scm-manager/ui-core";

type Props = {
  titleType?: string;
  title?: string;
};

const CITitle: FC<Props> = ({ titleType, title }) => {
  return (
    <>
      {(titleType || title) && (
        <span className="ml-2">
          {titleType && <span className="has-text-weight-bold">{titleType}: </span>}
          {title}
        </span>
      )}
    </>
  );
};

export const getStatusVariantForCIStatus = (ciStatus?: CIStatus[]) => {
  if (ciStatus && ciStatus.filter((ci) => ci.status === "FAILURE").length > 0) {
    return StatusVariants.DANGER;
  } else if (ciStatus && ciStatus.filter((ci) => ci.status === "UNSTABLE").length > 0) {
    return StatusVariants.WARNING;
  } else if (ciStatus && ciStatus.every((ci) => ci.status === "SUCCESS")) {
    return StatusVariants.SUCCESS;
  } else {
    return StatusVariants.IN_PROGRESS;
  }
};

export const getStatusVariantForDeployments = (deployments: Deployment[]) => {
  if (deployments.filter((deployment) => deployment.status === "FAILURE").length > 0) {
    return StatusVariants.DANGER;
  } else if (deployments.filter((deployment) => deployment.status === "PENDING").length > 0) {
    return StatusVariants.IN_PROGRESS;
  } else {
    return StatusVariants.SUCCESS;
  }
};

export const getTitleForCIStatus = (ciStatus?: CIStatus[]) => {
  if (ciStatus && ciStatus.filter((ci) => ci.status === "FAILURE").length > 0) {
    return "faulty";
  } else if (ciStatus && ciStatus.filter((ci) => ci.status === "UNSTABLE").length > 0) {
    return "unstable";
  } else if (ciStatus && ciStatus.every((ci) => ci.status === "SUCCESS")) {
    return "successful";
  } else {
    return "running";
  }
};

export const getColorForCIStatus = (ciStatus?: CIStatus[]) => {
  if (ciStatus && ciStatus.filter((ci) => ci.status === "FAILURE").length > 0) {
    return "danger";
  } else if (ciStatus && ciStatus.filter((ci) => ci.status === "UNSTABLE").length > 0) {
    return "warning";
  } else if (ciStatus && ciStatus.every((ci) => ci.status === "SUCCESS")) {
    return "success";
  } else {
    return "secondary";
  }
};

export default CITitle;
