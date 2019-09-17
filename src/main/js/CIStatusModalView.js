//@flow
import React from "react";
import { translate } from "react-i18next";
import injectSheet from "react-jss";
import { Modal } from "@scm-manager/ui-components";
import StatusIcon, {
  SuccessIcon,
  FailureIcon,
  UnstableIcon
} from "./StatusIcon";
import ModalRow from "./ModalRow";
import { getDisplayName } from "./CIStatus";

type Props = {
  ciStatus: any,
  onClose: () => void,

  //context props
  classes: any,
  t: string => string
};

const styles = {
  headDanger: {
    "& .modal-card-head": {
      backgroundColor: "var(--danger-25)"
    }
  }
};

class CIStatusModalView extends React.Component<Props> {
  render() {
    const { onClose, ciStatus, classes, t } = this.props;

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
        className={errors > 0 ? classes.headDanger : ""}
      />
    );
  }
}

export default injectSheet(styles)(translate("plugins")(CIStatusModalView));
