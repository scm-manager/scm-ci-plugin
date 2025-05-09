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

import React, { FC, useState } from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import CIStatusModalView from "./CIStatusModalView";
import StatusBar from "./StatusBar";
import { getStatusVariantForCIStatus } from "./CITitle";
import { Branch, Repository } from "@scm-manager/ui-types";
import { CIStatus, useCiStatus } from "./CIStatus";

type Props = {
  repository: Repository;
  pullRequest?: any;
  branch?: Branch;
};

const CIStatusBar: FC<Props> = ({ repository, pullRequest, branch }) => {
  const [showModal, setShowModal] = useState(false);
  const [icon, setIcon] = useState<string | undefined>();
  const { error, isLoading, data: ciStatus } = useCiStatus(repository, { pullRequest, branch }, (ci: CIStatus[]) => {
    setIcon(getStatusVariantForCIStatus(ci));
  });

  if (error) {
    return <ErrorNotification error={error} />;
  }
  if (isLoading) {
    return <Loading />;
  }
  if (!ciStatus) {
    return null;
  }

  return (
    <>
      {showModal && <CIStatusModalView onClose={() => setShowModal(false)} ciStatus={ciStatus} />}
      {icon && (
        <StatusBar
          icon={icon}
          backgroundColor="secondary"
          onClick={() => setShowModal(true)}
          ciStatus={ciStatus}
        />
      )}
    </>
  );
};

export default CIStatusBar;
