/**
 * Servlet context path set on `globalThis` by the Spring Boot SPA template
 * (`spa.template`). Empty when the app is served from the server root.
 */
export function getContextPath(): string {
  const fromPage = globalThis.__CONTEXT_PATH__
  return typeof fromPage === 'string' ? fromPage : ''
}
