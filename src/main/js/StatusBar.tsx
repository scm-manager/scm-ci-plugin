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
import classNames from "classnames";
import { withTranslation, WithTranslation } from "react-i18next";
import styled from "styled-components";
import { CIStatus } from "./CIStatus";

type Props = WithTranslation & {
  backgroundColor: string;
  icon: string;
  iconColor: string;
  onClick: () => void;
  ciStatus: CIStatus[];
};

const Notification = styled.div`
  padding: 1rem 1.25rem;
  line-height: 1;
  border-top: none !important;
`;

class StatusBar extends React.Component<Props> {
  render() {
    const { backgroundColor, icon, iconColor, ciStatus, onClick, t } = this.props;
    const errors =
      ciStatus && ciStatus.length > 0
        ? ciStatus.filter(ci => ci.status === "FAILURE" || ci.status === "UNSTABLE").length
        : 0;
    const hasAnalyzes = ciStatus && ciStatus.length !== 0;
    return (
      <Notification
        className={classNames(
          "media",
          `notification is-${backgroundColor}`,
          hasAnalyzes ? "has-cursor-pointer" : "",
          "mt-4",
          "mb-0"
        )}
        onClick={hasAnalyzes ? onClick : undefined}
      >
        <i className={`fas fa-${icon} fa-lg pr-2 has-text-${iconColor}`} />
        <span className="has-text-weight-bold">
          {t("scm-ci-plugin.statusbar.analysis", {
            count: ciStatus && ciStatus.length
          })}
        </span>
        <i className="fas fa-angle-right mx-2 my-0" />
        <span>
          {t("scm-ci-plugin.statusbar.error", {
            count: errors
          })}
        </span>
      </Notification>
    );
  }
}

export default withTranslation("plugins")(StatusBar);
