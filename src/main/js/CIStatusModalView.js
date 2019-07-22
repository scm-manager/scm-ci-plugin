//@flow
import React from "react";
import { Modal } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import StatusIcon from "./StatusIcon";

type Props = {
  t: string => string,
  ciStatus: any,
  onClose: () => void
};

class CIStatusModalView extends React.Component<Props> {
  render() {
    const {
      onClose,
      ciStatus,
      t
    } = this.props;

    const body = (
      <div className="content columns">
        <div className="column">
          {ciStatus.map(ci =>
            ci.status === "SUCCESS" ? (<StatusIcon color="success" size="1" icon="check-circle" title={ci.type + ": " + ci.name}/>):
            ci.status === "FAILURE" ? (<StatusIcon color="danger" size="1" icon="times-circle" title={ci.type + ": " + ci.name}/>):
            ci.status === "UNSTABLE" ? (<StatusIcon color="warning" size="1" icon="exclamation-circle" title={ci.type + ": " + ci.name}/>):
            (<StatusIcon color="light" size="1" icon="circle-notch" title={ci.type + ": " + ci.name}/>))}
        </div>
        <div className="column">
          {ciStatus.map(ci =>
            <div>
              <i className={"fas fa-chevron-right"}/>
              <a href={ci.url}>{t("scm-ci-plugin.modal.details")}</a>
            </div>)}
        </div>
      </div>
    );

    return (
      <Modal
        title={t("scm-ci-plugin.modal.title")}
        closeFunction={() => onClose()}
        body={body}
        active={true}
      />
    );
  }
}

export default translate("plugins")(CIStatusModalView);
