import { describe, expect, it } from 'vitest'

import { getLegacyLibraryUrl, getLegacyRootUrl } from './getLegacyLibraryUrl'

import type { SchemaListItem } from './api-helpers/types'

const endpoint = 'http://library.example/catalogo'
const singleSchema: SchemaListItem = { schema: 'single', name: 'Biblivre V' }
const publicSchema: SchemaListItem = {
  schema: 'public',
  name: 'Biblioteca Pública',
}

describe('getLegacyRootUrl', () => {
  it('adds a trailing slash so POST does not bounce off the context path', () => {
    expect(getLegacyRootUrl(endpoint)).toBe(`${endpoint}/`)
    expect(getLegacyRootUrl('http://library.example')).toBe(
      'http://library.example/',
    )
  })

  it('keeps a trailing slash that is already present', () => {
    expect(getLegacyRootUrl(`${endpoint}/`)).toBe(`${endpoint}/`)
  })
})

describe('getLegacyLibraryUrl', () => {
  it('falls back to the classic root when schemas or the active schema are missing', () => {
    expect(getLegacyLibraryUrl(undefined, 'single', endpoint)).toBe(
      `${endpoint}/`,
    )
    expect(getLegacyLibraryUrl([singleSchema], null, endpoint)).toBe(
      `${endpoint}/`,
    )
  })

  it('falls back to the classic root when the active schema is not in the list', () => {
    expect(getLegacyLibraryUrl([singleSchema], 'missing', endpoint)).toBe(
      `${endpoint}/`,
    )
  })

  it('points at the single schema path when it is the only library, including under multi-library', () => {
    expect(getLegacyLibraryUrl([singleSchema], 'single', endpoint)).toBe(
      `${endpoint}/single`,
    )
  })

  it('points at the active schema path when several libraries exist', () => {
    expect(
      getLegacyLibraryUrl([singleSchema, publicSchema], 'public', endpoint),
    ).toBe(`${endpoint}/public`)
  })
})
