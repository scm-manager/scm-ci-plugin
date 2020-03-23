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
import classNames from "classnames";
import { Repository, Changeset } from "@scm-manager/ui-types";
import { CIStatus } from "./CIStatus";
import StatusIcon, { SuccessIcon, FailureIcon, UnstableIcon } from "./StatusIcon";
import CIStatusModalView from "./CIStatusModalView";
import { getDisplayName } from "./CIStatus";
import styled from "styled-components";

type Props = WithTranslation & {
  repository: Repository;
  changeset: Changeset;
};

type State = {
  modalOpen: boolean;
};

const Wrapper = styled.div`
  margin: 0 0.35rem 0 1.1rem;
`;

const Flex = styled.div`
  line-height: 1.5rem;
`;

class CIStatusSummary extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      modalOpen: false
    };
  }

  render() {
    const { changeset, t } = this.props;
    const { modalOpen } = this.state;
    const ciStatus: CIStatus[] = changeset._embedded.ciStatus;
    if (!ciStatus) {
      return null;
    }

    let icon = null;
    if (ciStatus.length === 0) {
      icon = <StatusIcon />;
    } else if (ciStatus.filter(ci => ci.status === "FAILURE").length > 0) {
      icon = <FailureIcon />;
    } else if (ciStatus.filter(ci => ci.status === "UNSTABLE").length > 0) {
      icon = <UnstableIcon />;
    } else if (ciStatus.every(ci => ci.status === "SUCCESS")) {
      icon = <SuccessIcon />;
    } else {
      icon = <StatusIcon />;
    }

    const ciStatusModalView = modalOpen ? (
      <CIStatusModalView
        onClose={() =>
          this.setState({
            modalOpen: false
          })
        }
        ciStatus={ciStatus}
      />
    ) : null;

    const content = (
      <>
        {ciStatus.length === 0 && t("scm-ci-plugin.popover.noStatus")}
        {ciStatus.map(ci => (
          <>
            {ci.status === "SUCCESS" ? (
              <SuccessIcon titleType={ci.type} title={getDisplayName(ci)} />
            ) : ci.status === "FAILURE" ? (
              <FailureIcon titleType={ci.type} title={getDisplayName(ci)} />
            ) : ci.status === "UNSTABLE" ? (
              <UnstableIcon titleType={ci.type} title={getDisplayName(ci)} />
            ) : (
              <StatusIcon titleType={ci.type} title={getDisplayName(ci)} />
            )}
            <br />
          </>
        ))}
      </>
    );

    const hasAnalyzes = ciStatus && ciStatus.length !== 0;

    return (
      <>
        {ciStatusModalView}
        <Wrapper className="popover is-popover-top">
          <Flex className="popover-content">{content}</Flex>
          <div
            className={classNames("popover-trigger", hasAnalyzes ? "has-cursor-pointer" : "")}
            onClick={
              hasAnalyzes
                ? () =>
                    ciStatus.length > 0 &&
                    this.setState({
                      modalOpen: true
                    })
                : ""
            }
          >
            {icon}
          </div>
        </Wrapper>
      </>
    );
  }
}

export default withTranslation("plugins")(CIStatusSummary);
