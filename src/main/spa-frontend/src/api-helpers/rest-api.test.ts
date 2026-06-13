import { beforeEach, describe, expect, it } from 'vitest'

import { SCHEMA_STORAGE_KEY } from './constants'
import { defaultRestApiFetchOptions } from './rest-api'

describe('defaultRestApiFetchOptions', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('adds Accept, schema header, and credentials to the request', async () => {
    localStorage.setItem(SCHEMA_STORAGE_KEY, 'bib4template')

    const result = await defaultRestApiFetchOptions({
      init: {
        method: 'GET',
        headers: {},
      },
      context: {
        path: '/indexing_groups/biblio',
        method: 'GET',
        headers: {},
      },
    })

    expect(result.credentials).toBe('include')
    expect(result.headers).toMatchObject({
      Accept: 'application/json',
      'X-Biblivre-Schema': 'bib4template',
    })
  })

  it('preserves existing headers', async () => {
    const result = await defaultRestApiFetchOptions({
      init: {
        method: 'GET',
        headers: {
          'X-Custom': 'value',
        },
      },
      context: {
        path: '/indexing_groups/biblio',
        method: 'GET',
        headers: {},
      },
    })

    expect(result.headers).toMatchObject({
      'X-Custom': 'value',
      Accept: 'application/json',
    })
  })
})
