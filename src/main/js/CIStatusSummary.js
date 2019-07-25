// @flow
import React from "react";
import {Repository, Changeset} from "@scm-manager/ui-types";
import injectSheet from "react-jss";
import type {CIStatus} from "./CIStatus";
import classNames from "classnames";
import StatusIcon, {FailureIcon, PlaceholderIcon, SuccessIcon, UnstableIcon} from "./StatusIcon";
import { translate } from "react-i18next";
import CIStatusModalView from "./CIStatusModalView";

const styles = {
  wrapper: {
    height: "35px",
    marginLeft: "0.75rem"
  },
  flex: {
    lineHeight: "1.5rem"
  },
  trigger: {
    cursor: "pointer"
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
}

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
      icon = <PlaceholderIcon/>;
    } else if ( ciStatus.filter(ci => ci.status === "FAILURE").length > 0 ) {
      icon = <FailureIcon/>;
    } else if (ciStatus.filter(ci => ci.status === "UNSTABLE").length > 0) {
      icon = <UnstableIcon/>
    } else if(ciStatus.every(ci => ci.status === "SUCCESS")) {
      icon = <SuccessIcon/>;
    } else {
      icon = <PlaceholderIcon/>
    }

    const ciStatusModalView = modalOpen ?
      <CIStatusModalView
        onClose={() => this.setState({ modalOpen: false })}
        ciStatus={ciStatus}
      />
    : null;

    const content  = (
      <div>
        {ciStatus.length === 0 && t("scm-ci-plugin.popover.noStatus")}
        {ciStatus.map(ci =>
            ci.status === "SUCCESS" ? (<StatusIcon color="success" size="1" icon="check-circle" title={ci.type + ": " + ci.name}/>) :
            ci.status === "FAILURE" ? (<StatusIcon color="danger" size="1" icon="times-circle" title={ci.type + ": " + ci.name}/>):
            ci.status === "UNSTABLE" ? (<StatusIcon color="warning" size="1" icon="exclamation-circle" title={ci.type + ": " + ci.name}/>):
            (<StatusIcon color="light" size="1" icon="circle-notch" title={ci.type + ": " + ci.name}/>))}
      </div>
    );

    return (
      <>
        {ciStatusModalView}
        <div className={classNames(classes.wrapper, "popover is-popover-top")}>
          <div className={classNames(classes.flex, "popover-content")}>
            {content}
          </div>
          <div className={classNames(classes.trigger, "popover-trigger")} onClick={() => ciStatus.length > 0 && this.setState({modalOpen: true})}>
            {icon}
          </div>
        </div>
      </>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(CIStatusSummary));
