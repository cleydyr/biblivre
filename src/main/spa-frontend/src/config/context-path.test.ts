import { afterEach, describe, expect, it } from 'vitest'

import { getContextPath } from './context-path'

describe('getContextPath', () => {
  afterEach(() => {
    delete globalThis.__CONTEXT_PATH__
  })

  it('returns the servlet context path injected by the SPA page', () => {
    globalThis.__CONTEXT_PATH__ = '/catalogo'

    expect(getContextPath()).toBe('/catalogo')
  })

  it('returns an empty string when the app is served from the server root', () => {
    globalThis.__CONTEXT_PATH__ = ''

    expect(getContextPath()).toBe('')
  })

  it('returns an empty string when the page did not inject a context path', () => {
    expect(getContextPath()).toBe('')
  })
})
