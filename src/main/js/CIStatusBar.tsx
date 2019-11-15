import React from "react";
import { apiClient, ErrorNotification, Loading, NotFoundError } from "@scm-manager/ui-components";
import CIStatusModalView from "./CIStatusModalView";
import StatusBar from "./StatusBar";
import { getColor, getIcon } from "./StatusIcon";
import { Repository } from "@scm-manager/ui-types";

type Props = {
  repository: Repository;
  pullRequest: any;
};

type State = {
  ciStatus?: any;
  icon?: string;
  color?: string;
  modalOpen: boolean;
  error?: Error;
  loading: boolean;
};

export default class CIStatusBar extends React.Component<Props, State> {
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
    const url = repository._links.ciStatus.href.replace("{revision}", encodeURIComponent(pullRequest.source));
    this.setState({
      loading: true
    });
    apiClient.get(url)
      .then(response => response.json())
      .then(json => {
        this.setState({
          ciStatus: json._embedded.ciStatus,
          loading: false
        }, this.setStatus);
      })
      .catch(error => {
        if (error instanceof NotFoundError) {
          this.setState({
            loading: false
          });
        } else {
          this.setState({
            error,
            loading: false
          });
        }

      });
  };

  setStatus = () => {
    const { ciStatus } = this.state;
    this.setState({
      color: getColor(ciStatus),
      icon: getIcon(ciStatus)
    });
  };

  toggleModal = () => {
    this.setState(prevState => ({
      modalOpen: !prevState.modalOpen
    }));
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
        {modalOpen && <CIStatusModalView onClose={this.toggleModal} ciStatus={ciStatus} />}
        {color && icon && (
          <StatusBar
            icon={icon}
            backgroundColor={success ? "secondary" : color}
            iconColor={success ? color : color === "secondary" ? "grey-lighter" : "undefined"}
            onClick={this.toggleModal}
            ciStatus={ciStatus}
          />
        )}
      </>
    );
  }
}
