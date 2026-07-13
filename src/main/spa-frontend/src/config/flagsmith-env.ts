/**
 * Client-side Flagsmith config: set on `globalThis` by the Spring Boot SPA template
 * (`spa.template`). For `yarn dev` without that page, optional `VITE_*` vars in
 * `.env.local` are used.
 */
export function getFlagsmithEnvironmentId(): string | undefined {
  const fromPage = getFromPage('__FLAGSMITH_ENVIRONMENT_KEY__')
  return fromPage || import.meta.env.VITE_FLAGSMITH_ENVIRONMENT_KEY
}

export function getFlagsmithApiUrl(): string | undefined {
  const fromPage = getFromPage('__FLAGSMITH_API_URL__')
  return fromPage || import.meta.env.VITE_FLAGSMITH_API_URL
}

function getFromPage(
  key: '__FLAGSMITH_ENVIRONMENT_KEY__' | '__FLAGSMITH_API_URL__',
): string | undefined {
  return globalThis[key]
}
