import * as Sentry from '@sentry/react'

import { getMonitoringConfig } from './config'
import { applyScope, buildSentryOptions } from './sentry-options'

/** Starts error monitoring for the SPA when the server configured it. */
export function initSpaMonitoring() {
  const config = getMonitoringConfig()
  const options = buildSentryOptions(config)

  if (!options) {
    return
  }

  Sentry.init(options)
  applyScope(Sentry, config, 'spa')
}

function httpStatusOf(error: unknown): number | undefined {
  if (typeof error !== 'object' || error === null) {
    return undefined
  }

  const candidate = error as {
    status?: unknown
    response?: { status?: unknown }
  }
  const status = candidate.response?.status ?? candidate.status

  return typeof status === 'number' ? status : undefined
}

/**
 * Reports a failed query or mutation. Client errors (HTTP 4xx) are expected
 * outcomes such as validation or permission failures and are skipped.
 */
export function reportQueryError(error: unknown, kind: 'query' | 'mutation') {
  const status = httpStatusOf(error)

  if (status !== undefined && status >= 400 && status < 500) {
    return
  }

  Sentry.captureException(error, { tags: { 'react-query': kind } })
}
