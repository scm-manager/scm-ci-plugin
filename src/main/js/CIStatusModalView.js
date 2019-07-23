//@flow
import React from "react";
import { Modal } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import StatusIcon from "./StatusIcon";
import ModalRow from "./ModalRow";

type Props = {
  t: string => string,
  ciStatus: any,
  onClose: () => void,
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
            ci.status === "SUCCESS" ? (
              <ModalRow
                status={<StatusIcon color="success" size="1" icon="check-circle" title={ci.type + ": " + ci.name} titleColor="white" />}
                ciUrl={ci.url}
              />):
            ci.status === "FAILURE" ? (
              <ModalRow
                status={<StatusIcon color="danger" size="1" icon="times-circle" title={ci.type + ": " + ci.name} />}
                ciUrl={ci.url}
              />):
            ci.status === "UNSTABLE" ? (
              <ModalRow
                status={<StatusIcon color="warning" size="1" icon="exclamation-circle" title={ci.type + ": " + ci.name} />}
                ciUrl={ci.url} />):
            (<ModalRow
              status={<StatusIcon color="light" size="1" icon="circle-notch" title={ci.type + ": " + ci.name} />}
              ciUrl={ci.url}
              />
              ))}
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

export default (translate("plugins")(CIStatusModalView));
