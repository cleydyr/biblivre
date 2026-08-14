import { describe, expect, it } from 'vitest'

import { getLegacyLibraryUrl } from './getLegacyLibraryUrl'

import type { SchemaListItem } from './api-helpers/types'

const endpoint = 'http://library.example/catalogo'
const singleSchema: SchemaListItem = { schema: 'single', name: 'Biblivre V' }
const publicSchema: SchemaListItem = {
  schema: 'public',
  name: 'Biblioteca Pública',
}

describe('getLegacyLibraryUrl', () => {
  it('returns undefined when schemas or the active schema are missing', () => {
    expect(getLegacyLibraryUrl(undefined, 'single', endpoint)).toBeUndefined()
    expect(getLegacyLibraryUrl([singleSchema], null, endpoint)).toBeUndefined()
  })

  it('returns undefined when the active schema is not in the list', () => {
    expect(
      getLegacyLibraryUrl([singleSchema], 'missing', endpoint),
    ).toBeUndefined()
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
