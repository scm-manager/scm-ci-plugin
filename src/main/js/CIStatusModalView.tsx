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
import React from "react";
import { withTranslation, WithTranslation } from "react-i18next";
import { Modal } from "@scm-manager/ui-components";
import { CIStatus } from "./CIStatus";
import { getColor } from "./StatusIcon";
import CIStatusList from "./CIStatusList";

type Props = WithTranslation & {
  ciStatus: CIStatus;
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
    const color = ciStatus && ciStatus.length > 0 ? getColor(ciStatus) : "";

    return (
      <Modal
        title={
          <strong className={`has-text-${color === "warning" ? "warning-invert" : color === "secondary" ? "default" : "white"}`}>
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
