/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import React from "react";
import { apiClient, ErrorNotification, Loading, NotFoundError } from "@scm-manager/ui-components";
import CIStatusModalView from "./CIStatusModalView";
import StatusBar from "./StatusBar";
import { getColor, getIcon } from "./StatusIcon";
import { Branch, Repository, Link } from "@scm-manager/ui-types";
import { CIStatus } from "./CIStatus";

type Props = {
  repository: Repository;
  pullRequest?: any;
  branch?: Branch;
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

  fetchCIStatusFromUrl = (url?: string) => {
    apiClient
      .get(url)
      .then(response => response.json())
      .then(json => {
        this.setState(
          {
            ciStatus: json._embedded.ciStatus,
            loading: false
          },
          this.setStatus
        );
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

  fetchCIStatusFromPullRequest = () => {
    const { pullRequest } = this.props;
    const url = (pullRequest._links.ciStatus as Link)?.href;
    this.fetchCIStatusFromUrl(url);
  };

  fetchCIStatusFromBranch = () => {
    const { branch } = this.props;
    const url = (branch._links.details as Link)?.href;
    this.fetchCIStatusFromUrl(url);
  };

  fetchCIStatus = () => {
    const { pullRequest, branch } = this.props;
    if (pullRequest) {
      this.fetchCIStatusFromPullRequest();
    } else if (branch) {
      this.fetchCIStatusFromBranch();
    }
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

    const success = ciStatus && ciStatus.every((ci: CIStatus) => ci.status === "SUCCESS");

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
