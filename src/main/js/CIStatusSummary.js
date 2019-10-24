// @flow
import React from "react";
import { withTranslation, WithTranslation } from "react-i18next";
import classNames from "classnames";
import type { Repository, Changeset } from "@scm-manager/ui-types";
import type { CIStatus } from "./CIStatus";
import StatusIcon, {
  SuccessIcon,
  FailureIcon,
  UnstableIcon
} from "./StatusIcon";
import CIStatusModalView from "./CIStatusModalView";
import { getDisplayName } from "./CIStatus";
import styled from "styled-components";

type Props = WithTranslation & {
  repository: Repository,
  changeset: Changeset
};

type State = {
  modalOpen: boolean
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
        onClose={() => this.setState({ modalOpen: false })}
        ciStatus={ciStatus}
      />
    ) : null;

    const content = (
      <>
        {ciStatus.length === 0 && t("scm-ci-plugin.popover.noStatus")}
        {ciStatus.map(ci =>
          ci.status === "SUCCESS" ? (
            <SuccessIcon titleType={ci.type} title={getDisplayName(ci)} />
          ) : ci.status === "FAILURE" ? (
            <FailureIcon titleType={ci.type} title={getDisplayName(ci)} />
          ) : ci.status === "UNSTABLE" ? (
            <UnstableIcon titleType={ci.type} title={getDisplayName(ci)} />
          ) : (
            <StatusIcon titleType={ci.type} title={getDisplayName(ci)} />
          )
        )}
      </>
    );

    const hasAnalyzes = ciStatus && ciStatus.length !== 0;

    return (
      <>
        {ciStatusModalView}
        <Wrapper className="popover is-popover-top">
          <Flex className="popover-content">
            {content}
          </Flex>
          <div
            className={classNames(
              "popover-trigger",
              hasAnalyzes ? "has-cursor-pointer" : ""
            )}
            onClick={
              hasAnalyzes
                ? () =>
                    ciStatus.length > 0 && this.setState({ modalOpen: true })
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
