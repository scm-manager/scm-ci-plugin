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
