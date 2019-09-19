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
import { getColor } from "./StatusIcon";

type Props = {
  ciStatus: any,
  onClose: () => void,

  //context props
  t: (key: string, params?: Object) => string
};

class CIStatusModalView extends React.Component<Props> {
  render() {
    const { onClose, ciStatus, t } = this.props;

    const body = (
      <>
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
      </>
    );
    const errors =
      ciStatus && ciStatus.length > 0
        ? ciStatus.filter(
            ci => ci.status === "FAILURE" || ci.status === "UNSTABLE"
          ).length
        : 0;
    const color = ciStatus && ciStatus.length > 0 ? getColor(ciStatus) : "";

    return (
      <Modal
        title={
          <strong
            className={`has-text-${
              color === "warning"
                ? "brown"
                : color === "secondary"
                ? "default"
                : "white"
            }`}
          >
            {t("scm-ci-plugin.modal.title", {
              count: errors
            })}
          </strong>
        }
        closeFunction={() => onClose()}
        body={body}
        active={true}
        headColor={color}
      />
    );
  }
}

export default translate("plugins")(CIStatusModalView);
