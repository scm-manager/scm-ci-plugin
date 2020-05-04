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

import React, { FC, useEffect, useState } from "react";
import { InputField } from "@scm-manager/ui-components";
import { useTranslation } from "react-i18next";

type Configuration = {
  type: string;
};

type Props = {
  configurationChanged: (newRuleConfiguration: Configuration, valid: boolean) => void;
};

const CIStatusOfTypeSuccessRuleConfiguration: FC<Props> = ({ configurationChanged }) => {
  const [t] = useTranslation("plugins");
  const [typeOfSuccessful, setTypeOfSuccessful] = useState<string | undefined>();
  const [validationError, setValidationError] = useState(false);

  const onValueChange = (val: string) => {
    setTypeOfSuccessful(val);
    const trimmedVal = val?.trim();
    if (trimmedVal && trimmedVal.length > 0) {
      setValidationError(false);
      configurationChanged({ type: trimmedVal }, true);
    } else {
      setValidationError(true);
      configurationChanged({ type: "" }, false);
    }
  };

  useEffect(() => configurationChanged({ type: "" }, false), []);

  return (
    <InputField
      type="text"
      value={typeOfSuccessful}
      label={t("workflow.rule.CIStatusOfTypeSuccessRule.form.type.label")}
      helpText={t("workflow.rule.CIStatusOfTypeSuccessRule.form.type.helpText")}
      validationError={validationError}
      errorMessage={t("workflow.rule.CIStatusOfTypeSuccessRule.form.type.errorMessage")}
      autofocus={true}
      onChange={onValueChange}
    />
  );
};

export default CIStatusOfTypeSuccessRuleConfiguration;
