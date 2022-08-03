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
import { BranchDetails, Changeset, Repository } from "@scm-manager/ui-types";
import { NoStyleButton, SmallLoadingSpinner } from "@scm-manager/ui-components";
import { CIStatus } from "./CIStatus";
import StatusIcon, { FailureIcon, SuccessIcon, UnstableIcon } from "./StatusIcon";
import CIStatusModalView from "./CIStatusModalView";
import styled from "styled-components";
import CIStatusList from "./CIStatusList";
import * as Tooltip from "@radix-ui/react-tooltip";

const StyledArrow = styled(Tooltip.Arrow)`
  fill: var(--scm-popover-border-color);
`;

const PopoverWrapper = styled.div`
  z-index: 500;
`;

type Props = {
  repository: Repository;
  changeset?: Changeset;
  details?: BranchDetails;
  explicitCiStatus?: CIStatus[] | undefined;
};

const Wrapper = styled.div`
  margin: 0 0.35rem 0 1.1rem;
`;

const CIStatusSummary: FC<Props> = ({ changeset, details, explicitCiStatus }) => {
  const [modalOpen, setModalOpen] = useState(false);
  const [t] = useTranslation("plugins");

  if (!changeset && !details && !explicitCiStatus) {
    return <SmallLoadingSpinner />;
  }

  let ciStatus: CIStatus[] | undefined;
  if (changeset?._embedded?.ciStatus) {
    ciStatus = changeset._embedded.ciStatus as CIStatus[];
  } else if (details?._embedded?.ciStatus) {
    ciStatus = details._embedded.ciStatus as CIStatus[];
  } else if (explicitCiStatus) {
    ciStatus = explicitCiStatus;
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

  const errors =
    ciStatus && ciStatus.length > 0
      ? ciStatus.filter(ci => ci.status === "FAILURE" || ci.status === "UNSTABLE").length
      : 0;

  return (
    <>
      {ciStatusModalView}
      <Wrapper className="is-relative">
        <Tooltip.Provider>
          <Tooltip.Root>
            <Tooltip.Trigger asChild={true}>
              <NoStyleButton>{icon}</NoStyleButton>
            </Tooltip.Trigger>
            <Tooltip.Portal>
              <Tooltip.Content>
                <PopoverWrapper className="box m-0 popover">
                  <h1 className="has-text-weight-bold is-size-5">
                    {t("scm-ci-plugin.modal.title", {
                      count: errors
                    })}
                  </h1>
                  <hr className="my-2" />
                  <CIStatusList ciStatus={ciStatus} />
                </PopoverWrapper>
                <StyledArrow />
              </Tooltip.Content>
            </Tooltip.Portal>
          </Tooltip.Root>
        </Tooltip.Provider>
      </Wrapper>
    </>
  );
};

export default CIStatusSummary;
