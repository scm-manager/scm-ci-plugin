import React from "react";
import {Column} from "@scm-manager/ui-components";
import CIStatusSummary from "./CIStatusSummary";
import {useCiStatus} from "./CIStatus";

const CiStatusWrapper = ({repository, pullRequest}) => {
  const {data} = useCiStatus(repository, {pullRequest});
  return <CIStatusSummary explicitCiStatus={data} repository={repository}/>
};

export default ({repository, t}) => <Column header={t("scm-ci-plugin.statusbar.title")}>
  {(pullRequest) => <CiStatusWrapper repository={repository} pullRequest={pullRequest}/>}
</Column>;
