//@flow
import React from "react";
import { Modal } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import {FailureIcon, PlaceholderIcon, SuccessIcon, UnstableIcon} from "./StatusIcon";
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
        <div>
          {ciStatus.map(ci =>
            ci.status === "SUCCESS" ? (
              <ModalRow
                status={<SuccessIcon title={ci.type + ": " + ci.name} />}
                ciUrl={ci.url}
              />):
            ci.status === "FAILURE" ? (
              <ModalRow
                status={<FailureIcon title={ci.type + ": " + ci.name} />}
                ciUrl={ci.url}
              />):
            ci.status === "UNSTABLE" ? (
              <ModalRow
                status={<UnstableIcon title={ci.type + ": " + ci.name} />}
                ciUrl={ci.url} />):
            (<ModalRow
              status={<PlaceholderIcon title={ci.type + ": " + ci.name} />}
              ciUrl={ci.url}
              />
              ))}
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
