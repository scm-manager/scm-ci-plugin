// @flow
import React from "react";
import classNames from "classnames";
import injectSheet from "react-jss";
import { translate } from "react-i18next";

type Props = {
  status: any,
  ciUrl: any,

  //context props
  classes: any,
  t: string => string
};

const styles = {
  entry: {
    borderBottom: "solid 1px rgba(10, 10, 10, 0.2)",
    padding: "1rem 0rem",
    display: "flex",
    flexDirection: "row"
  },
  left: {
    flex: "1"
  },
  paddingIcon: {
    paddingRight: "0.25rem"
  }
};

class ModalRow extends React.Component<Props> {
  render() {
    const { status, ciUrl, classes, t } = this.props;
    return (
      <div className={classNames(classes.entry)}>
        <div className={classes.left}>{status}</div>
        <div className={"is-pulled-right"}>
          <i
            className={classNames(classes.paddingIcon, "fas fa-chevron-right")}
          />
          <a target={"_blank"} href={ciUrl}>
            {t("scm-ci-plugin.modal.details")}
          </a>
        </div>
      </div>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(ModalRow));
