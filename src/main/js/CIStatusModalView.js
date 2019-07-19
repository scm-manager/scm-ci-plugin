//@flow
import React from "react";
import { Modal } from "@scm-manager/ui-components";
import { translate } from "react-i18next";

type Props = {
  t: string => string,
  onClose: () => void
};

class CIStatusModalView extends React.Component<Props> {
  render() {
    const {
      onClose,
      t
    } = this.props;

    const body = (
      <div className="content">
      </div>
    );

    return (
      <Modal
        title={t(
          "scm-ci-plugin.show-ci-status.modal-view"
        )}
        closeFunction={() => onClose()}
        body={body}
        active={true}
      />
    );
  }
}

export default translate("plugins")(CIStatusModalView);
