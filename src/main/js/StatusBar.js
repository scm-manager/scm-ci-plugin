// @flow
import React from "react";
import classNames from "classnames";
import injectSheet from "react-jss";
import { translate } from "react-i18next";

type Props = {
  backgroundColor: string,
  icon: string,
  iconColor: string,
  titleColor: string,
  onClick: () => void,
  ciStatus: any,

  //context props
  classes: any,
  t: string => string
};

const styles = {
  bar: {
    lineHeight: "2.5rem",
    margin: "1rem 0rem",
    paddingLeft: "0.75rem",
    cursor: "pointer"
  },
  message: {
    margin: "0rem 0.25rem"
  },
  icon: {
    paddingRight: "0.25rem"
  }
};

class StatusBar extends React.Component<Props> {
  render() {
    const {
      backgroundColor,
      icon,
      titleColor,
      iconColor,
      ciStatus,
      onClick,
      classes,
      t
    } = this.props;
    const errors =
      ciStatus && ciStatus.length > 0
        ? ciStatus.filter(
            ci => ci.status === "FAILURE" || ci.status === "UNSTABLE"
          ).length
        : 0;
    return (
      <div
        className={classNames(
          classes.bar,
          `is-full-width has-background-${backgroundColor} has-text-${titleColor}`
        )}
        onClick={onClick}
      >
        <i
          className={classNames(
            classes.icon,
            `fas fa-${icon} has-text-${iconColor}`
          )}
        />
        <span className={classNames(classes.message, "has-text-weight-bold")}>
          {t("scm-ci-plugin.statusbar.analysis", {
            count: ciStatus && ciStatus.length
          })}
        </span>
        <i className={"fas fa-chevron-right"} />
        <span className={classNames(classes.message)}>
          {t("scm-ci-plugin.statusbar.error", { count: errors })}
        </span>
      </div>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(StatusBar));
