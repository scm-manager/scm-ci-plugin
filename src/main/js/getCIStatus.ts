import { apiClient } from "@scm-manager/ui-components";

export function getCIStatus(url: string) {
  return apiClient
    .get(url)
    .then(response => {
      return response;
    })
    .catch(err => {
      return {
        error: err
      };
    });
}
