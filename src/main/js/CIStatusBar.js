//@flow
import React from "react";
import CIStatusModalView from "./CIStatusModalView";
import {getCIStatus} from "./cistatus";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import StatusBar from "./StatusBar";
import {getColor, getIcon} from "./StatusIcon";

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
}

class CIStatusBar extends React.Component<Props, State> {

  constructor(props: Props) {
    super(props);

    this.state = {
      modalOpen: false,
      loading: false
    };
  }

  componentDidMount() {
    this.fetchCIStatus();
  };

  fetchCIStatus = () => {
    const { repository, pullRequest } = this.props;
    const url = repository._links.changesets.href.replace("repositories", "ci") + encodeURIComponent(pullRequest.source);
    this.setState({ loading : true });
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
        this.setState({error, loading: false});
      });
  };

  setStatus = () => {
    const { ciStatus } = this.state;
    this.setState({ icon: getIcon(ciStatus), color: getColor(ciStatus) });
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
      return <ErrorNotification error={error}/>
    }
    if (loading) {
      return <Loading/>
    }

    const success = (ciStatus && ciStatus.every(ci => ci.status === "SUCCESS"));

    return (
      <>
        {
          modalOpen &&
          <CIStatusModalView
            onClose={this.onClose}
            ciStatus={ciStatus}
          />
        }
        {
          color && icon &&
          <StatusBar
            icon={icon}
            backgroundColor={success ? "white-ter" : color}
            iconColor={success ? color : "white"}
            titleColor={success ? "dark" : "white"}
            onClick={this.openModal}
            ciStatus={ciStatus}
          />
        }
      </>
    )
  }
}

export default CIStatusBar;