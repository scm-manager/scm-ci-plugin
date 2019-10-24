import React from "react";
import classNames from "classnames";
import { withTranslation, WithTranslation } from "react-i18next";
import styled from "styled-components";

type Props = WithTranslation & {
  backgroundColor: string;
  icon: string;
  iconColor: string;
  onClick: () => void;
  ciStatus: any;
};

const Notification = styled.div`
  margin-top: 1rem;
  margin-bottom: 0 !important;
  padding: 1rem 1.25rem;
  line-height: 1;
  border-top: none !important;
`;

const Icon = styled.i`
  padding-right: 0.5rem;
`;

const AngleRight = styled.i`
  margin: 0 0.5rem;
`;

class StatusBar extends React.Component<Props> {
  render() {
    const { backgroundColor, icon, iconColor, ciStatus, onClick, t } = this.props;
    const errors =
      ciStatus && ciStatus.length > 0
        ? ciStatus.filter(ci => ci.status === "FAILURE" || ci.status === "UNSTABLE").length
        : 0;
    const hasAnalyzes = ciStatus && ciStatus.length !== 0;
    return (
      <Notification
        className={classNames("media", `notification is-${backgroundColor}`, hasAnalyzes ? "has-cursor-pointer" : "")}
        onClick={hasAnalyzes ? onClick : ""}
      >
        <Icon className={`fas fa-${icon} fa-lg has-text-${iconColor}`} />
        <span className="has-text-weight-bold">
          {t("scm-ci-plugin.statusbar.analysis", {
            count: ciStatus && ciStatus.length
          })}
        </span>
        <AngleRight className="fas fa-angle-right" />
        <span>
          {t("scm-ci-plugin.statusbar.error", {
            count: errors
          })}
        </span>
      </Notification>
    );
  }
}

export default withTranslation("plugins")(StatusBar);
