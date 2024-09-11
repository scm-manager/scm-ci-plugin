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
