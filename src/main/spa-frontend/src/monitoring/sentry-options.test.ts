import { describe, expect, it, vi } from 'vitest'

import {
  applyScope,
  buildSentryOptions,
  scrubBreadcrumb,
  scrubUrl,
} from './sentry-options'

describe('scrubUrl', () => {
  it('filters sensitive query parameters and keeps the rest', () => {
    expect(scrubUrl('/bib/?module=login&password=hunter2&page=2#top')).toBe(
      '/bib/?module=login&password=[Filtered]&page=2#top',
    )
  })

  it('leaves URLs without a query string alone', () => {
    expect(scrubUrl('/bib/circulation')).toBe('/bib/circulation')
  })

  it('filters token-like names regardless of case', () => {
    expect(scrubUrl('https://x/a?API_KEY=1&Token=2')).toBe(
      'https://x/a?API_KEY=[Filtered]&Token=[Filtered]',
    )
  })
})

describe('scrubBreadcrumb', () => {
  it('scrubs the url of xhr and fetch breadcrumbs', () => {
    const crumb = scrubBreadcrumb({
      category: 'xhr',
      data: { url: '/x?secret=1', status_code: 500 },
    })

    expect(crumb.data).toEqual({
      url: '/x?secret=[Filtered]',
      status_code: 500,
    })
  })
})

describe('buildSentryOptions', () => {
  it('is disabled without a DSN', () => {
    expect(buildSentryOptions({})).toBeUndefined()
  })

  it('routes events through the tunnel and limits collected data', () => {
    const options = buildSentryOptions({
      dsn: 'https://key@errors.example.org/1',
      tunnel: '/Biblivre6/api/v2/client-errors',
      release: 'biblivre@6.0.0',
    })

    expect(options?.tunnel).toBe('/Biblivre6/api/v2/client-errors')
    expect(options?.release).toBe('biblivre@6.0.0')
    expect(options?.sampleRate).toBe(1)
    expect(options?.dataCollection?.cookies).toBe(false)
  })

  it('leaves out release-health sessions', () => {
    const options = buildSentryOptions({ dsn: 'https://key@example.org/1' })
    const integrations = options?.integrations as (
      defaults: { name: string }[],
    ) => { name: string }[]

    expect(
      integrations([{ name: 'BrowserSession' }, { name: 'GlobalHandlers' }]),
    ).toEqual([{ name: 'GlobalHandlers' }])
  })
})

describe('applyScope', () => {
  it('tags schema, app and user id', () => {
    const sentry = { setTag: vi.fn(), setUser: vi.fn() }

    applyScope(sentry, { schema: 'bib1', userId: '42' }, 'spa')

    expect(sentry.setTag).toHaveBeenCalledWith('app', 'spa')
    expect(sentry.setTag).toHaveBeenCalledWith('schema', 'bib1')
    expect(sentry.setUser).toHaveBeenCalledWith({ id: '42' })
  })
})
