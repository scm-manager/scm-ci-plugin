// @flow
import React from "react";
import {Repository, Changeset} from "@scm-manager/ui-types";
import { Tooltip } from "@scm-manager/ui-components";
import injectSheet from "react-jss";
import type {CIStatus} from "./CIStatus";
import classNames from "classnames";
import {FailureIcon, PlaceholderIcon, SuccessIcon, UnstableIcon} from "./StatusIcon";

const styles = {
  wrapper: {
    height: "45px",
    marginLeft: "0.75rem"
  }
};

type Props = {
  repository: Repository,
  changeset: Changeset,

  // context props
  classes: any
};

class CIStatusSummary extends React.Component<Props> {

  render() {
    const {changeset, classes} = this.props;
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

    const message  = (
      <div>
        <SuccessIcon/> Jenkins
        <FailureIcon/> Sonar
      </div>
    );

    return (
      <Tooltip className={classNames(classes.wrapper, "is-tooltip")} location="top" message={ message }>
        {icon}
      </Tooltip>
    );
  }

}

export default injectSheet(styles)(CIStatusSummary);
