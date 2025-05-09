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

import React from "react";
import classNames from "classnames";
import { withTranslation, WithTranslation } from "react-i18next";
import styled from "styled-components";
import { CIStatus } from "./CIStatus";
import { Button, Icon } from "@scm-manager/ui-core";
import { StatusIcon } from "@scm-manager/ui-core";

type Props = WithTranslation & {
  backgroundColor: string;
  icon: string;
  onClick: () => void;
  ciStatus: CIStatus[];
};

type NotificationProps = {
  isPointer: boolean;
};

export const Notification = styled(Button)<NotificationProps>`
  width: 100%;
  justify-content: start;
  align-content: center;
  height: max-content;
  border: none !important;
  font-weight: normal;
  padding: 1rem 1.25rem 1rem 1.75rem;
  cursor: ${({ isPointer }) => (isPointer ? "pointer" : "default")};
`;

class StatusBar extends React.Component<Props> {
  render() {
    const { backgroundColor, icon, ciStatus, onClick, t } = this.props;
    const errors =
      ciStatus && ciStatus.length > 0
        ? ciStatus.filter((ci) => ci.status === "FAILURE" || ci.status === "UNSTABLE").length
        : 0;
    const hasAnalyzes = ciStatus && ciStatus.length !== 0;

    if (ciStatus && ciStatus.length > 0) {
      return (
        <Notification
          className={classNames("media", `notification is-${backgroundColor}`)}
          onClick={hasAnalyzes ? onClick : undefined}
          isPointer={hasAnalyzes}
          aria-disabled={hasAnalyzes}
        >
          <StatusIcon variant={icon} className={classNames("is-medium mr-2")} />
          <span className="has-text-weight-bold">
            {t("scm-ci-plugin.statusbar.analysis", {
              count: ciStatus && ciStatus.length,
            })}
          </span>
          <Icon>angle-right</Icon>
          <span>
            {t("scm-ci-plugin.statusbar.error", {
              count: errors,
            })}
          </span>
        </Notification>
      );
    }
    return <></>;
  }
}

export default withTranslation("plugins")(StatusBar);
