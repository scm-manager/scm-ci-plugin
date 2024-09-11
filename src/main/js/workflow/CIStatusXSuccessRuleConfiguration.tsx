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
