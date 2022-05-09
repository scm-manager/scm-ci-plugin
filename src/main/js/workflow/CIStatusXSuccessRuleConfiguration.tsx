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
  numberOfSuccessful: number;
  ignoreChangesetStatus: boolean;
};

type Props = {
  configurationChanged: (newRuleConfiguration: Configuration, valid: boolean) => void;
};

const CIStatusXSuccessRuleConfiguration: FC<Props> = ({ configurationChanged }) => {
  const [t] = useTranslation("plugins");
  const [numberOfSuccessful, setNumberOfSuccessful] = useState<string | undefined>();
  const [numberOfSuccessfulValue, setNumberOfSuccessfulValue] = useState<number>(0);
  const [ignoreChangesetStatus, setIgnoreChangesetStatus] = useState<boolean>(false);
  const [validationError, setValidationError] = useState(false);

  const onValueChange = (val: string) => {
    setNumberOfSuccessful(val);
    const numberVal = parseInt(val);
    if (!isNaN(numberVal) && numberVal > 0) {
      setValidationError(false);
      setNumberOfSuccessfulValue(numberVal);
      configurationChanged(
        {
          numberOfSuccessful: numberVal,
          ...createConfigurationFor(ignoreChangesetStatus)
        },
        true
      );
    } else {
      setValidationError(true);
      setNumberOfSuccessfulValue(0);
      configurationChanged(
        {
          numberOfSuccessful: 0,
          ...createConfigurationFor(ignoreChangesetStatus)
        },
        false
      );
    }
  };

  const onIgnoreChangesetStatusChange = (val: boolean) => {
    setIgnoreChangesetStatus(val);
    configurationChanged(
      {
        numberOfSuccessful: numberOfSuccessfulValue,
        ...createConfigurationFor(val)
      },
      true
    );
  };

  useEffect(() => configurationChanged({ numberOfSuccessful: 0, ...createConfigurationFor(false) }, false), []);

  return (
    <>
      <InputField
        type="number"
        value={numberOfSuccessful}
        label={t("workflow.rule.CIStatusXSuccessRule.form.numberOfSuccessful.label")}
        helpText={t("workflow.rule.CIStatusXSuccessRule.form.numberOfSuccessful.helpText")}
        validationError={validationError}
        errorMessage={t("workflow.rule.CIStatusXSuccessRule.form.numberOfSuccessful.errorMessage")}
        autofocus={true}
        onChange={onValueChange}
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

export default CIStatusXSuccessRuleConfiguration;
