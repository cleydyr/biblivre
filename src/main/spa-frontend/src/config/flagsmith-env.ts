/**
 * Client-side Flagsmith config: set on `globalThis` by the Spring Boot SPA template
 * (`spa.template`). For `yarn dev` without that page, optional `VITE_*` vars in
 * `.env.local` are used.
 */
export function getFlagsmithEnvironmentId(): string | undefined {
  return getFromPage('__FLAGSMITH_ENVIRONMENT_KEY__') ?? import.meta.env.VITE_FLAGSMITH_ENVIRONMENT_KEY ?? ''
}

export function getFlagsmithApiUrl(): string | undefined {
  return getFromPage('__FLAGSMITH_API_URL__') ?? import.meta.env.VITE_FLAGSMITH_API_URL ?? ''
}

function getFromPage(key: keyof typeof globalThis): string {
  return globalThis[key]
}