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
    display: "flex",
    flexDirection: "row",
    padding: "1rem 0rem",
    borderBottom: "1px solid rgba(219, 219, 219, 0.5)"
  },
  left: {
    flex: "1"
  },
  iconPadding: {
    paddingRight: "0.25rem"
  },
  linkColor: {
    color: "initial"
  }
};

class ModalRow extends React.Component<Props> {
  render() {
    const { status, ciUrl, classes, t } = this.props;
    return (
      <div className={classNames(classes.entry)}>
        <div className={classes.left}>{status}</div>
        <div className="is-pulled-right">
          <i
            className={classNames("fas", "fa-angle-right", classes.iconPadding)}
          />
          <a
            className={classes.linkColor}
            href={ciUrl}
            target="_blank"
            rel="noopener noreferrer"
          >
            {t("scm-ci-plugin.modal.details")}
          </a>
        </div>
      </div>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(ModalRow));
