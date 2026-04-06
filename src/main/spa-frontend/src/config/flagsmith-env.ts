/**
 * Client-side Flagsmith environment key: set on `window` by the Spring Boot SPA
 * template (`spa.template`). For `yarn dev` without that page, optional
 * `VITE_FLAGSMITH_ENVIRONMENT_KEY` in `.env.local` is used.
 */
export function getFlagsmithEnvironmentId(): string {
  const fromPage =
    typeof window !== 'undefined' ? window.__FLAGSMITH_ENVIRONMENT_KEY__ : undefined

  if (fromPage !== undefined && fromPage !== '') {
    return fromPage
  }

  return import.meta.env.VITE_FLAGSMITH_ENVIRONMENT_KEY ?? ''
}
