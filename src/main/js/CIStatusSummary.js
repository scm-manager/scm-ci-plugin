// @flow
import React from "react";
import { translate } from "react-i18next";
import injectSheet from "react-jss";
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

const styles = {
  wrapper: {
    margin: "0 0.35rem 0 1.1rem"
  },
  flex: {
    lineHeight: "1.5rem"
  }
};

type Props = {
  repository: Repository,
  changeset: Changeset,

  // context props
  classes: any,
  t: string => string
};

type State = {
  modalOpen: boolean
};

class CIStatusSummary extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      modalOpen: false
    };
  }

  render() {
    const { changeset, classes, t } = this.props;
    const { modalOpen } = this.state;
    const ciStatus: CIStatus[] | undefined = changeset._embedded.ciStatus;
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
      <div>
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
      </div>
    );

    const hasAnalyzes = ciStatus && ciStatus.length !== 0;

    return (
      <>
        {ciStatusModalView}
        <div className={classNames(classes.wrapper, "popover is-popover-top")}>
          <div className={classNames(classes.flex, "popover-content")}>
            {content}
          </div>
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
        </div>
      </>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(CIStatusSummary));
