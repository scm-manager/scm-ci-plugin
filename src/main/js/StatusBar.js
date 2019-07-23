import React from "react";
import classNames from "classnames";
import injectSheet from "react-jss";
import { translate } from "react-i18next";


type Props = {
  color: string,
  icon : string,
  onClick: () => void,
  ciStatus: any,
  classes: any
};

const styles = {
  bar: {
    lineHeight: "2.5rem",
    margin: "10px 0px",
    paddingLeft: "10px"
  },
  message: {
    margin: "0px 5px",
  }
};

class StatusBar extends React.Component<Props> {
  render() {
    const { color, icon, ciStatus, onClick, classes, t } = this.props;
    const errors = (ciStatus && ciStatus.length > 0 ? ciStatus.filter(ci => ci.status === "FAILURE" || ci.status === "UNSTABLE").length : 0);
    return (
      <div className={classNames(classes.bar, `is-full-width has-background-${color} has-text-white`)} onClick={onClick}>
        <i className={`fas fa-1x fa-${icon}`}/>
        <span className={classNames(classes.message, "has-text-weight-bold")}>
          {(ciStatus && ciStatus.length)} {t("scm-review-plugin.pull-request.ci-status-bar.analysis-message")}
        </span>
        <i className={"fas fa-chevron-right"}/>
        <span className={classNames(classes.message)}>
          {errors} {t("scm-review-plugin.pull-request.ci-status-bar.result-message")}
        </span>
      </div>
    )
  }
}

export default injectSheet(styles)(translate("plugins")(StatusBar));
