// @flow
import React from "react";
import classNames from "classnames";
import injectSheet from "react-jss";
import { translate } from "react-i18next";

type Props = {
  backgroundColor: string,
  icon: string,
  iconColor: string,
  onClick: () => void,
  ciStatus: any,

  //context props
  classes: any,
  t: string => string
};

const styles = {
  notification: {
    margin: "1rem 0rem",
    padding: "1rem 1.25rem",
    lineHeight: "0.75rem",
    cursor: "pointer"
  },
  icon: {
    paddingRight: "0.5rem"
  },
  angleRight: {
    margin: "0 0.5rem"
  }
};

class StatusBar extends React.Component<Props> {
  render() {
    const {
      backgroundColor,
      icon,
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
          `notification is-${backgroundColor}`,
          classes.notification
        )}
        onClick={onClick}
      >
        <i
          className={classNames(
            classes.icon,
            `fas fa-${icon} fa-lg has-text-${iconColor}`
          )}
        />
        <span className="has-text-weight-bold">
          {t("scm-ci-plugin.statusbar.analysis", {
            count: ciStatus && ciStatus.length
          })}
        </span>
        <i
          className={classNames("fas", "fa-angle-right", classes.angleRight)}
        />
        <span>{t("scm-ci-plugin.statusbar.error", { count: errors })}</span>
      </div>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(StatusBar));
