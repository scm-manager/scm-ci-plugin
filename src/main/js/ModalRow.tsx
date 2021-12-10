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
import { useTranslation, WithTranslation } from "react-i18next";
import styled from "styled-components";

type Props = WithTranslation & {
  status: any;
  ciUrl: any;
};

const OverlayLink = styled.a`
  width: 90%;
  position: absolute;
  height: calc(80px - 1.5rem);
  pointer-events: all;
  border-radius: 4px;
  :hover {
    cursor: pointer;
  }
`;

const Entry = styled.div`
  display: flex;
  flex-direction: row;
  padding: 1rem 0;

  // css adjacent with same component
  & + & {
    border-top: 1px solid rgba(219, 219, 219, 0.5);
  }
`;

const Left = styled.div`
  flex: 1;
`;

const ModalRow: FC<Props> = ({ status, ciUrl }) => {
  const [t] = useTranslation("plugins");

  return (
    <>
      <OverlayLink
        href={ciUrl}
        target="_blank"
        rel="noopener noreferrer"
        className="has-hover-background-blue"
        aria-label={t("overview.ariaLabel", { name: status })}
      />
      <Entry>
        <Left>{status}</Left>
      </Entry>
    </>
  );
};

export default ModalRow;
