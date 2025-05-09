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
import { withTranslation, WithTranslation } from "react-i18next";
import { Modal } from "@scm-manager/ui-components";
import { CIStatus } from "./CIStatus";
import { getColorForCIStatus } from "./CITitle";
import CIStatusList from "./CIStatusList";

type Props = WithTranslation & {
  ciStatus: CIStatus[];
  onClose: () => void;
};

class CIStatusModalView extends React.Component<Props> {
  render() {
    const { onClose, ciStatus, t } = this.props;

    const body = <CIStatusList ciStatus={ciStatus} />;
    const errors =
      ciStatus && ciStatus.length > 0
        ? ciStatus.filter(ci => ci.status === "FAILURE" || ci.status === "UNSTABLE").length
        : 0;
    const color = ciStatus && ciStatus.length > 0 ? getColorForCIStatus(ciStatus) : "";

    return (
      <Modal
        title={
          <strong
            className={`has-text-${
              color === "warning" ? "warning-invert" : color === "secondary" ? "default" : "white"
            }`}
          >
            {t("scm-ci-plugin.modal.title", {
              count: errors
            })}
          </strong>
        }
        closeFunction={() => onClose()}
        body={body}
        active={true}
        headColor={color}
      />
    );
  }
}

export default withTranslation("plugins")(CIStatusModalView);
