// @flow
import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import CIStatusSummary from "./CIStatusSummary"

const CIStatusModalView = props => {
    return (
    <>
      <CIStatusModalView />
    </>
    );
};

binder.bind("ciPlugin.modalView", CIStatusModalView);


const CIStatus = props => {
  return (
    <>
      <CIStatus/>
    </>
  );
};



binder.bind("changeset.right", CIStatusSummary);

