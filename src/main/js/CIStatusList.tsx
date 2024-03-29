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
import React, { FC } from "react";
import StatusIcon, { SuccessIcon, FailureIcon, UnstableIcon } from "./StatusIcon";
import ModalRow from "./ModalRow";
import { CIStatus, getDisplayName } from "./CIStatus";

type Props = {
  ciStatus?: CIStatus[];
};

const createRow = (ci: CIStatus) => {
  switch (ci.status) {
    case "SUCCESS":
      return <ModalRow status={<SuccessIcon titleType={ci.type} title={getDisplayName(ci)} />} ciUrl={ci.url} />;
    case "FAILURE":
      return <ModalRow status={<FailureIcon titleType={ci.type} title={getDisplayName(ci)} />} ciUrl={ci.url} />;
    case "UNSTABLE":
      return <ModalRow status={<UnstableIcon titleType={ci.type} title={getDisplayName(ci)} />} ciUrl={ci.url} />;
    default:
      return <ModalRow status={<StatusIcon titleType={ci.type} title={getDisplayName(ci)} />} ciUrl={ci.url} />;
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
