import type { Breadcrumb, BrowserOptions, ErrorEvent } from '@sentry/browser'

import type { MonitoringConfig } from './config'

const FILTERED = '[Filtered]'

const SENSITIVE_PARAM = /pass|token|secret|key|auth/i

/** Replaces the values of sensitive query string parameters in a URL. */
export function scrubUrl(url: string): string {
  const queryStart = url.indexOf('?')

  if (queryStart < 0) {
    return url
  }

  const [query, hash = ''] = url.slice(queryStart + 1).split('#', 2)

  const scrubbed = query
    .split('&')
    .map((pair) => {
      const [name] = pair.split('=', 1)
      return SENSITIVE_PARAM.test(decodeURIComponent(name))
        ? `${name}=${FILTERED}`
        : pair
    })
    .join('&')

  const fragment = hash ? `#${hash}` : ''

  return `${url.slice(0, queryStart)}?${scrubbed}${fragment}`
}

export function scrubEvent(event: ErrorEvent): ErrorEvent {
  if (event.request?.url) {
    event.request.url = scrubUrl(event.request.url)
  }

  if (event.request?.query_string) {
    event.request.query_string = FILTERED
  }

  return event
}

export function scrubBreadcrumb(breadcrumb: Breadcrumb): Breadcrumb {
  const url = breadcrumb.data?.url

  if (typeof url === 'string') {
    breadcrumb.data = { ...breadcrumb.data, url: scrubUrl(url) }
  }

  return breadcrumb
}

/**
 * Sentry SDK options shared by the legacy pages and the SPA. Returns
 * `undefined` when the server did not configure a DSN.
 */
export function buildSentryOptions(
  config: MonitoringConfig,
): BrowserOptions | undefined {
  if (!config.dsn) {
    return undefined
  }

  return {
    dsn: config.dsn,
    tunnel: config.tunnel,
    release: config.release,
    environment: config.environment,
    sampleRate: config.sampleRate ?? 1,
    // Only what was agreed for monitoring: URL, breadcrumbs, schema and user id.
    dataCollection: {
      userInfo: false,
      cookies: false,
      httpHeaders: false,
      httpBodies: [],
      urlQueryParams: true,
    },
    ignoreErrors: [
      'ResizeObserver loop limit exceeded',
      'ResizeObserver loop completed with undelivered notifications',
    ],
    denyUrls: [
      /extensions\//i,
      /^chrome(-extension)?:\/\//i,
      /^moz-extension:\/\//i,
      /^safari(-web)?-extension:\/\//i,
    ],
    // Release-health sessions would send an envelope on every page load and,
    // behind a library's shared IP, eat the tunnel's per-IP rate limit.
    integrations: (defaults) =>
      defaults.filter((integration) => integration.name !== 'BrowserSession'),
    beforeSend: scrubEvent,
    beforeBreadcrumb: scrubBreadcrumb,
  }
}

/** Tags every event with the library (schema) and the logged-in user id. */
export function applyScope(
  sentry: {
    setTag: (key: string, value: string) => void
    setUser: (user: { id: string } | null) => void
  },
  config: MonitoringConfig,
  app: 'legacy' | 'spa',
) {
  sentry.setTag('app', app)

  if (config.schema) {
    sentry.setTag('schema', config.schema)
  }

  if (config.userId) {
    sentry.setUser({ id: config.userId })
  }
}
