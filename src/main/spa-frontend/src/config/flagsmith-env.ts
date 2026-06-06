/**
 * Client-side Flagsmith config: set on `globalThis` by the Spring Boot SPA template
 * (`spa.template`). For `yarn dev` without that page, optional `VITE_*` vars in
 * `.env.local` are used.
 */
export function getFlagsmithEnvironmentId(): string {
  const fromPage = globalThis.__FLAGSMITH_ENVIRONMENT_KEY__

  if (fromPage !== undefined && fromPage !== '') {
    return fromPage
  }

  return import.meta.env.VITE_FLAGSMITH_ENVIRONMENT_KEY ?? ''
}

export function getFlagsmithApiUrl(): string {
  const fromPage = globalThis.__FLAGSMITH_API_URL__

  if (fromPage !== undefined && fromPage !== '') {
    return fromPage
  }

  return import.meta.env.VITE_FLAGSMITH_API_URL ?? ''
}
