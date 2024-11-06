import type { BiblivreLegacyModule } from "./modules";

export async function fetchJSONFromServer(
  module: BiblivreLegacyModule,
  action: string,
  otherParams: object,
) {
  const response = await fetch("/", {
    method: "POST",
    headers: {
      "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
    },
    body: new URLSearchParams({
      controller: "json",
      module,
      action,
      ...otherParams,
    }).toString(),
  });

  return response.json();
}
