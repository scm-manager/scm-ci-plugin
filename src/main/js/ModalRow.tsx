import React from "react";
import { withTranslation, WithTranslation } from "react-i18next";
import styled from "styled-components";

type Props = WithTranslation & {
  status: any;
  ciUrl: any;
};

const Entry = styled.div`
  display: flex;
  flex-direction: row;
  padding: 1rem 0rem;
  border-bottom: 1px solid rgba(219, 219, 219, 0.5);
`;

const Left = styled.div`
  flex: 1;
`;

const Icon = styled.i`
  padding-right: 0.25rem;
`;

const LinkColor = styled.a`
  color: initial;
`;

class ModalRow extends React.Component<Props> {
  render() {
    const { status, ciUrl, t } = this.props;
    return (
      <Entry>
        <Left>{status}</Left>
        <div className="is-pulled-right">
          <Icon className="fas fa-angle-right" />
          <LinkColor href={ciUrl} target="_blank" rel="noopener noreferrer">
            {t("scm-ci-plugin.modal.details")}
          </LinkColor>
        </div>
      </Entry>
    );
  }
}

export default withTranslation("plugins")(ModalRow);
