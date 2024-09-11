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
import {Icon} from "@scm-manager/ui-buttons";

type BaseProps = {
  titleType?: string;
  title?: string;
};
type Props = BaseProps & {
  color?: string;
  icon?: string;
  size?: string;
};

const StatusIcon: FC<Props> = ({ color = "secondary", icon = "circle-notch", size = "1x", titleType, title }) => {
  return (
    <>
      <Icon>{`${icon} has-text-${color} fa-${size}`}</Icon>
      {(titleType || title) && (
        <span className="ml-2">
          {titleType && <span className="has-text-weight-bold">{titleType}: </span>}
          {title}
        </span>
      )}
    </>
  );
};

export const getColor = (ciStatus?: CIStatus[]) => {
  if (ciStatus && ciStatus.filter(ci => ci.status === "FAILURE").length > 0) {
    return "danger";
  } else if (ciStatus && ciStatus.filter(ci => ci.status === "UNSTABLE").length > 0) {
    return "warning";
  } else if (ciStatus && ciStatus.every(ci => ci.status === "SUCCESS")) {
    return "success";
  } else {
    return "secondary";
  }
};

export const getIcon = (ciStatus?: CIStatus[]) => {
  if (
    ciStatus &&
    (ciStatus.filter(ci => ci.status === "FAILURE").length > 0 ||
      ciStatus.filter(ci => ci.status === "UNSTABLE").length > 0)
  ) {
    return "exclamation-triangle";
  } else if (ciStatus && ciStatus.every(ci => ci.status === "SUCCESS")) {
    return "check-circle";
  } else {
    return "circle-notch";
  }
};

export const getTitle = (ciStatus?: CIStatus[]) => {
  if (ciStatus && ciStatus.filter(ci => ci.status === "FAILURE").length > 0) {
    return "faulty";
  } else if (ciStatus && ciStatus.filter(ci => ci.status === "UNSTABLE").length > 0) {
    return "unstable";
  } else if (ciStatus && ciStatus.every(ci => ci.status === "SUCCESS")) {
    return "successful";
  } else {
    return "running";
  }
};

export const SuccessIcon: FC<BaseProps> = props => <StatusIcon color="success" icon="check-circle" {...props} />;
export const FailureIcon: FC<BaseProps> = props => <StatusIcon color="danger" icon="exclamation-triangle" {...props} />;
export const UnstableIcon: FC<BaseProps> = props => (
  <StatusIcon color="warning" icon="exclamation-triangle" {...props} />
);

export default StatusIcon;
