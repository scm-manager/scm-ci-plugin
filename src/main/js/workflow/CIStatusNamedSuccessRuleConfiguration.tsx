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
import { Checkbox, InputField } from "@scm-manager/ui-components";
import { useTranslation } from "react-i18next";
import { BasicConfiguration, createConfigurationFor } from "./BasicConfiguration";

type Configuration = BasicConfiguration & {
  type: string | undefined;
  name: string | undefined;
};

type Props = {
  configurationChanged: (newRuleConfiguration: Configuration, valid: boolean) => void;
};

const CIStatusNamedSuccessRuleConfiguration: FC<Props> = ({ configurationChanged }) => {
  const [t] = useTranslation("plugins");
  const [typeOfSuccessful, setTypeOfSuccessful] = useState("");
  const [nameOfSuccessful, setNameOfSuccessful] = useState("");
  const [ignoreChangesetStatus, setIgnoreChangesetStatus] = useState<boolean>(false);
  const [validationError, setValidationError] = useState({
    name: false,
    type: false
  });

  const onTypeChange = (val: string) => {
    setTypeOfSuccessful(val);
    const trimmedVal = val?.trim();
    if (trimmedVal && trimmedVal.length > 0) {
      setValidationError({ type: false, name: validationError.name });
      configurationChanged(
        {
          type: trimmedVal,
          name: nameOfSuccessful,
          ...createConfigurationFor(ignoreChangesetStatus)
        },
        true
      );
    } else {
      setValidationError({ type: true, name: validationError.name });
      configurationChanged(
        {
          type: undefined,
          name: undefined,
          ...createConfigurationFor(ignoreChangesetStatus)
        },
        false
      );
    }
  };

  const onNameChange = (val: string) => {
    setNameOfSuccessful(val);
    const trimmedVal = val?.trim();
    if (trimmedVal && trimmedVal.length > 0) {
      setValidationError({ type: validationError.type, name: false });
      configurationChanged(
        {
          name: trimmedVal,
          type: typeOfSuccessful,
          ...createConfigurationFor(ignoreChangesetStatus)
        },
        true
      );
    } else {
      setValidationError({ type: validationError.type, name: true });
      configurationChanged(
        { type: undefined, name: undefined, ...createConfigurationFor(ignoreChangesetStatus) },
        false
      );
    }
  };

  const onIgnoreChangesetStatusChange = (val: boolean) => {
    setIgnoreChangesetStatus(val);
    configurationChanged(
      {
        name: nameOfSuccessful,
        type: typeOfSuccessful,
        ...createConfigurationFor(val)
      },
      true
    );
  };

  useEffect(
    () => configurationChanged({ type: undefined, name: undefined, ...createConfigurationFor(false) }, false),
    []
  );

  return (
    <>
      <InputField
        type="text"
        value={typeOfSuccessful}
        label={t("workflow.rule.CIStatusNamedSuccessRule.form.type.label")}
        helpText={t("workflow.rule.CIStatusNamedSuccessRule.form.type.helpText")}
        validationError={validationError.type}
        errorMessage={t("workflow.rule.CIStatusNamedSuccessRule.form.type.errorMessage")}
        autofocus={true}
        onChange={onTypeChange}
      />
      <InputField
        type="text"
        value={nameOfSuccessful}
        label={t("workflow.rule.CIStatusNamedSuccessRule.form.name.label")}
        helpText={t("workflow.rule.CIStatusNamedSuccessRule.form.name.helpText")}
        validationError={validationError.name}
        errorMessage={t("workflow.rule.CIStatusNamedSuccessRule.form.name.errorMessage")}
        onChange={onNameChange}
      />
      <Checkbox
        checked={ignoreChangesetStatus}
        label={t("workflow.rule.CIStatusRule.form.ignoreChangesetStatus.label")}
        helpText={t("workflow.rule.CIStatusRule.form.ignoreChangesetStatus.helpText")}
        onChange={onIgnoreChangesetStatusChange}
      />
    </>
  );
};

export default CIStatusNamedSuccessRuleConfiguration;
