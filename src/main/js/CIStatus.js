//@flow
export type CIStatus = {
  type: string,
  name: string,
  displayName?: string,
  status: string,
  url: string
};

export const getDisplayName = (ciStatus: CIStatus) => ciStatus.displayName ? ciStatus.displayName : ciStatus.name;
