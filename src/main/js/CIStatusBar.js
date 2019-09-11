//@flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import CIStatusModalView from "./CIStatusModalView";
import { getCIStatus } from "./getCIStatus";
import StatusBar from "./StatusBar";
import { getColor, getIcon } from "./StatusIcon";

type Props = {
  repository: any,
  pullRequest: any,

  // context props
  t: string => string,
  classes: any
};

type State = {
  ciStatus: any,
  icon: string,
  color: string,
  modalOpen: boolean,
  error: Error,
  loading: any
};

class CIStatusBar extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      modalOpen: false,
      loading: false
    };
  }

  componentDidMount() {
    this.props.repository._links.ciStatus && this.fetchCIStatus();
  }

  fetchCIStatus = () => {
    const { repository, pullRequest } = this.props;
    const url = repository._links.ciStatus.href.replace(
      "{revision}",
      encodeURIComponent(pullRequest.source)
    );
    this.setState({ loading: true });
    getCIStatus(url)
      .then(response => response.json())
      .then(json => {
        this.setState({
          ciStatus: json._embedded.ciStatus,
          loading: false
        });
        this.setStatus();
      })
      .catch(error => {
        this.setState({ error, loading: false });
      });
  };

  setStatus = () => {
    const { ciStatus } = this.state;
    this.setState({ color: getColor(ciStatus), icon: getIcon(ciStatus) });
  };

  onClose = () => {
    this.setState({ modalOpen: false });
  };

  openModal = () => {
    this.setState({ modalOpen: true });
  };

  render() {
    const { ciStatus, modalOpen, color, icon, loading, error } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }
    if (loading) {
      return <Loading />;
    }

    const success = ciStatus && ciStatus.every(ci => ci.status === "SUCCESS");

    return (
      <>
        {modalOpen && (
          <CIStatusModalView onClose={this.onClose} ciStatus={ciStatus} />
        )}
        {color && icon && (
          <StatusBar
            icon={icon}
            backgroundColor={success ? "secondary" : color}
            iconColor={
              success ? color : color === "secondary" ? "grey-lighter" : "white"
            }
            onClick={this.openModal}
            ciStatus={ciStatus}
          />
        )}
      </>
    );
  }
}

export default CIStatusBar;
