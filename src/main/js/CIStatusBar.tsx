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
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import CIStatusModalView from "./CIStatusModalView";
import StatusBar from "./StatusBar";
import { getColor, getIcon } from "./StatusIcon";
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
  const [color, setColor] = useState<string | undefined>();
  const { error, isLoading, data: ciStatus } = useCiStatus(repository, { pullRequest, branch }, (ci: CIStatus[]) => {
    setColor(getColor(ci));
    setIcon(getIcon(ci));
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

  const success = ciStatus?.every((ci: CIStatus) => ci.status === "SUCCESS");

  return (
    <>
      {showModal && <CIStatusModalView onClose={() => setShowModal(false)} ciStatus={ciStatus} />}
      {color && icon && (
        <StatusBar
          icon={icon}
          backgroundColor={success ? "secondary" : color}
          iconColor={success ? color : color === "secondary" ? "secondary" : "undefined"}
          onClick={() => setShowModal(true)}
          ciStatus={ciStatus}
        />
      )}
    </>
  );
};

export default CIStatusBar;
