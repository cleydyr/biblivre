/**
 * Error monitoring for the legacy JSP/jQuery pages. Built as a classic script
 * (`static/scripts/biblivre.monitoring.js`, see vite.monitoring.config.ts) and
 * loaded by `head/start.jsp` before jQuery, so errors thrown while the other
 * scripts load are captured too.
 */
import {
  addBreadcrumb,
  captureMessage,
  init as initSentry,
  setTag,
  setUser,
} from '@sentry/browser'

import { getMonitoringConfig } from './config'
import { applyScope, buildSentryOptions, scrubUrl } from './sentry-options'

export interface AjaxFailure {
  url?: string
  method?: string
  status: number
  statusText?: string
  error?: string
  module?: string
  action?: string
}

export interface BiblivreMonitoringApi {
  captureAjaxFailure: (failure: AjaxFailure) => void
  breadcrumb: (category: string, message: string) => void
}

declare global {
  interface Window {
    BiblivreMonitoring?: BiblivreMonitoringApi
  }
}

let leavingPage = false

function captureAjaxFailure(failure: AjaxFailure) {
  // Requests cut short by navigating away fail with status 0; they are noise.
  if (failure.status === 0 && leavingPage) {
    return
  }

  const target =
    failure.module || failure.action
      ? `${failure.module ?? '?'}.${failure.action ?? '?'}`
      : scrubUrl(failure.url ?? '')
  const outcome =
    failure.status === 0 ? 'network error' : `HTTP ${failure.status}`

  captureMessage(`AJAX ${outcome} on ${target}`, {
    level: 'error',
    fingerprint: [
      'ajax-failure',
      failure.module ?? '',
      failure.action ?? '',
      String(failure.status),
    ],
    tags: { 'ajax.status': String(failure.status) },
    contexts: {
      ajax: {
        url: scrubUrl(failure.url ?? ''),
        method: failure.method,
        status: failure.status,
        statusText: failure.statusText,
        error: failure.error,
        module: failure.module,
        action: failure.action,
      },
    },
  })
}

function init() {
  const config = getMonitoringConfig()
  const options = buildSentryOptions(config)

  if (!options) {
    return
  }

  initSentry(options)
  applyScope({ setTag, setUser }, config, 'legacy')

  window.addEventListener('pagehide', () => {
    leavingPage = true
  })
  window.addEventListener('beforeunload', () => {
    leavingPage = true
  })

  window.BiblivreMonitoring = {
    captureAjaxFailure,
    breadcrumb: (category, message) =>
      addBreadcrumb({ category, message, level: 'error' }),
  }
}

init()
