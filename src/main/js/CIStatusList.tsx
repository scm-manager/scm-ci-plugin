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
import CITitle from "./CITitle";
import ModalRow from "./ModalRow";
import { CIStatus, getDisplayName } from "./CIStatus";
import { StatusVariants } from "@scm-manager/ui-core";

type Props = {
  ciStatus?: CIStatus[];
};

const createRow = (ci: CIStatus) => {
  switch (ci.status) {
    case "SUCCESS":
      return (
        <ModalRow
          variant={StatusVariants.SUCCESS}
          status={<CITitle titleType={ci.type} title={getDisplayName(ci)} />}
          ciUrl={ci.url}
        />
      );
    case "FAILURE":
      return (
        <ModalRow
          variant={StatusVariants.DANGER}
          status={<CITitle titleType={ci.type} title={getDisplayName(ci)} />}
          ciUrl={ci.url}
        />
      );
    case "UNSTABLE":
      return (
        <ModalRow
          variant={StatusVariants.WARNING}
          status={<CITitle titleType={ci.type} title={getDisplayName(ci)} />}
          ciUrl={ci.url}
        />
      );
    default:
      return (
        <ModalRow
          variant={StatusVariants.IN_PROGRESS}
          status={<CITitle titleType={ci.type} title={getDisplayName(ci)} />}
          ciUrl={ci.url}
        />
      );
  }
};

const CIStatusList: FC<Props> = ({ ciStatus }) => {
  if (!ciStatus) {
    return null;
  }
  return (
    <>
      <hr className="mb-0 mt-4" />
      {ciStatus.map((ci, key) => (
        <>
          {createRow(ci)}
          {key < ciStatus.length - 1 ? <hr className="m-0" /> : null}
        </>
      ))}
    </>
  );
};

export default CIStatusList;
