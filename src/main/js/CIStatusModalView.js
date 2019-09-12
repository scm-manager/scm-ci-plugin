//@flow
import React from "react";
import { translate } from "react-i18next";
import { Modal } from "@scm-manager/ui-components";
import StatusIcon, {
  SuccessIcon,
  FailureIcon,
  UnstableIcon
} from "./StatusIcon";
import ModalRow from "./ModalRow";
import { getDisplayName } from "./CIStatus";

type Props = {
  t: string => string,
  ciStatus: any,
  onClose: () => void
};

class CIStatusModalView extends React.Component<Props> {
  render() {
    const { onClose, ciStatus, t } = this.props;

    const body = (
      <div>
        {ciStatus.map(ci =>
          ci.status === "SUCCESS" ? (
            <ModalRow
              status={
                <SuccessIcon
                  titleType={ci.type}
                  title={getDisplayName(ci)}
                  size="lg"
                />
              }
              ciUrl={ci.url}
            />
          ) : ci.status === "FAILURE" ? (
            <ModalRow
              status={
                <FailureIcon
                  titleType={ci.type}
                  title={getDisplayName(ci)}
                  size="lg"
                />
              }
              ciUrl={ci.url}
            />
          ) : ci.status === "UNSTABLE" ? (
            <ModalRow
              status={
                <UnstableIcon
                  titleType={ci.type}
                  title={getDisplayName(ci)}
                  size="lg"
                />
              }
              ciUrl={ci.url}
            />
          ) : (
            <ModalRow
              status={
                <StatusIcon
                  titleType={ci.type}
                  title={getDisplayName(ci)}
                  size="lg"
                />
              }
              ciUrl={ci.url}
            />
          )
        )}
      </div>
    );
    const errors =
      ciStatus && ciStatus.length > 0
        ? ciStatus.filter(
            ci => ci.status === "FAILURE" || ci.status === "UNSTABLE"
          ).length
        : 0;

    return (
      <Modal
        title={
          <strong
            className={errors > 0 ? "has-text-danger" : "has-text-success"}
          >
            {t("scm-ci-plugin.modal.title", {
              count: errors
            })}
          </strong>
        }
        closeFunction={() => onClose()}
        body={body}
        active={true}
      />
    );
  }
}

export default translate("plugins")(CIStatusModalView);
