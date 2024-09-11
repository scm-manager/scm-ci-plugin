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
  type: string;
};

type Props = {
  configurationChanged: (newRuleConfiguration: Configuration, valid: boolean) => void;
};

const CIStatusOfTypeSuccessRuleConfiguration: FC<Props> = ({ configurationChanged }) => {
  const [t] = useTranslation("plugins");
  const [typeOfSuccessful, setTypeOfSuccessful] = useState("");
  const [ignoreChangesetStatus, setIgnoreChangesetStatus] = useState<boolean>(false);
  const [validationError, setValidationError] = useState(false);

  const onValueChange = (val: string) => {
    setTypeOfSuccessful(val);
    const trimmedVal = val?.trim();
    if (trimmedVal && trimmedVal.length > 0) {
      setValidationError(false);
      configurationChanged({ type: trimmedVal, ...createConfigurationFor(ignoreChangesetStatus) }, true);
    } else {
      setValidationError(true);
      configurationChanged({ type: "", ...createConfigurationFor(false) }, false);
    }
  };

  useEffect(() => configurationChanged({ type: "", ...createConfigurationFor(false) }, false), []);

  const onIgnoreChangesetStatusChange = (val: boolean) => {
    setIgnoreChangesetStatus(val);
    configurationChanged(
      {
        type: typeOfSuccessful,
        ...createConfigurationFor(val)
      },
      true
    );
  };

  return (
    <>
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
      <Checkbox
        checked={ignoreChangesetStatus}
        label={t("workflow.rule.CIStatusRule.form.ignoreChangesetStatus.label")}
        helpText={t("workflow.rule.CIStatusRule.form.ignoreChangesetStatus.helpText")}
        onChange={onIgnoreChangesetStatusChange}
      />
    </>
  );
};

export default CIStatusOfTypeSuccessRuleConfiguration;
