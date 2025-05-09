/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import React, { FC } from "react";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import { StatusIcon } from "@scm-manager/ui-core";

type Props = {
  status: any;
  ciUrl: any;
  variant: any;
};

export const OverlayLink = styled.a`
  display: flex;
  align-items: center;
  width: 100%;
  height: calc(80px - 1.5rem);
  pointer-events: all;
  border-radius: 4px;

  :hover {
    cursor: pointer;
  }
`;

const ModalRow: FC<Props> = ({ status, variant, ciUrl }) => {
  const [t] = useTranslation("plugins");

  return (
    <>
      <div className="is-flex is-flex-direction-row px-0 py-4">
        <StatusIcon iconSize="md" variant={variant} />
        <OverlayLink
          href={ciUrl}
          target="_blank"
          rel="noopener noreferrer"
          className="has-hover-background-blue"
          aria-label={t("overview.ariaLabel", { name: status })}
        >
          <span className="px-2 has-text-default">{status}</span>
        </OverlayLink>
      </div>
    </>
  );
};

export default ModalRow;
