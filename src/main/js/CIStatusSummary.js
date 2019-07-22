// @flow

import React from "react";
import {Repository, Changeset} from "@scm-manager/ui-types";
import injectSheet from "react-jss";
import type {CIStatus} from "./CIStatus";
import classNames from "classnames";
import StatusIcon, {FailureIcon, PlaceholderIcon, SuccessIcon, UnstableIcon} from "./StatusIcon";

const styles = {
  wrapper: {
    height: "45px",
    marginLeft: "0.75rem"
  },
  flex: {
    display: "flex"
  },
  popover: {
    flexDirection: "row"
  }
};

type Props = {
  repository: Repository,
  changeset: Changeset,

  // context props
  classes: any,
  t: string => string
};

class CIStatusSummary extends React.Component<Props> {

  render() {
    const {changeset, classes, t} = this.props;
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

    const content  = (
      <div>
        {ciStatus.length === 0 && "ciPlugin.popover.noStatus"}
        {
          ciStatus.map(ci =>
          ci.status === "SUCCESS" ? (<StatusIcon color="success" size="1" icon="check-circle" title={ci.name}/>) :
          ci.status === "FAILURE" ? (<StatusIcon color="danger" size="1" icon="times-circle" title={ci.name}/>):
          ci.status === "UNSTABLE" ? (<StatusIcon color="warning" size="1" icon="exclamation-circle" title={ci.name}/>):
            (<StatusIcon color="light" size="1" icon="circle-notch" title={ci.name}/>))
        }
      </div>
    );

    return (
      <div className={classNames(classes.wrapper, "popover is-popover-top")}>
        <div className={classNames("popover-content has-background-grey-dark has-text-white")}>
          {content}
        </div>
        <div className="popover-trigger">
          {icon}
        </div>
      </div>
    );
  }
}

export default injectSheet(styles)(CIStatusSummary);
