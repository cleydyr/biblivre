import type { InitOverrideFunction } from "./generated-sources";

export function getSchemaFromURL() {
  return window.location.pathname.split("/")[1] || "bcuniaodosaber";
}

export function baseEndpointPath() {
  return `${window.location.origin}/api/v2`;
}

export const DEFAULT_FETCH_OPTIONS: InitOverrideFunction = async ({
  init,
}) => ({
  ...init,
  headers: {
    ...init.headers,
    "X-Biblivre-Schema": getSchemaFromURL(),
    Accept: "application/json",
  },
});
