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
import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import classNames from "classnames";
import { Repository, Changeset, BranchDetails } from "@scm-manager/ui-types";
import { Popover, SmallLoadingSpinner, usePopover } from "@scm-manager/ui-components";
import { CIStatus } from "./CIStatus";
import StatusIcon, { SuccessIcon, FailureIcon, UnstableIcon } from "./StatusIcon";
import CIStatusModalView from "./CIStatusModalView";
import styled from "styled-components";
import CIStatusList from "./CIStatusList";

type Props = {
  repository: Repository;
  changeset?: Changeset;
  details?: BranchDetails;
};

const Wrapper = styled.div`
  margin: 0 0.35rem 0 1.1rem;
`;

const CIStatusSummary: FC<Props> = ({ repository, changeset, details }) => {
  const [modalOpen, setModalOpen] = useState(false);
  const { popoverProps, triggerProps } = usePopover();
  const [t] = useTranslation("plugins");

  if (!changeset && !details) {
    return <SmallLoadingSpinner />;
  }

  let ciStatus: CIStatus[] | undefined;
  if (changeset?._embedded?.ciStatus) {
    ciStatus = changeset._embedded.ciStatus as CIStatus[];
  } else if (details?._embedded?.ciStatus) {
    ciStatus = details._embedded.ciStatus as CIStatus[];
  } else {
    return null;
  }

  let icon;
  if (!ciStatus || ciStatus.length === 0) {
    icon = <StatusIcon />;
  } else if (ciStatus.filter(ci => ci.status === "FAILURE").length > 0) {
    icon = <FailureIcon />;
  } else if (ciStatus.filter(ci => ci.status === "UNSTABLE").length > 0) {
    icon = <UnstableIcon />;
  } else if (ciStatus.every(ci => ci.status === "SUCCESS")) {
    icon = <SuccessIcon />;
  } else {
    icon = <StatusIcon />;
  }

  const ciStatusModalView =
    ciStatus && modalOpen ? <CIStatusModalView onClose={() => setModalOpen(false)} ciStatus={ciStatus} /> : null;

  const hasAnalyzes = ciStatus && ciStatus.length !== 0;

  const errors =
    ciStatus && ciStatus.length > 0
      ? ciStatus.filter(ci => ci.status === "FAILURE" || ci.status === "UNSTABLE").length
      : 0;

  return (
    <>
      {ciStatusModalView}
      <Wrapper className="is-relative">
        <Popover
          title={
            <h1 className="has-text-weight-bold is-size-5">
              {t("scm-ci-plugin.modal.title", {
                count: errors
              })}
            </h1>
          }
          width={400}
          {...popoverProps}
        >
          <CIStatusList ciStatus={ciStatus}/>
        </Popover>
        <div
          className={classNames("popover-trigger", hasAnalyzes ? "has-cursor-pointer" : "")}
          {...triggerProps}
        >
          {icon}
        </div>
      </Wrapper>
    </>
  );
};

export default CIStatusSummary;
