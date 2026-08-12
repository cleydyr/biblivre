import { readFileSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'
import { createContext, runInContext } from 'node:vm'

import { beforeAll, describe, expect, it } from 'vitest'

type AuthorNameParts = {
  surname: string
  givenName: string
}

type CatalogingCutterApi = {
  parsePersonalName: (marcName: string) => AuthorNameParts
  parseCorporateName: (marcName: string) => AuthorNameParts
  workLetter: (title: string, nonFilingChars?: string | number | null) => string
  buildAuthorCode: (
    surname: string,
    givenName: string,
    title: string,
    nonFilingChars?: string | number | null,
  ) => string | null
}

const scriptsDirectory = resolve(
  dirname(fileURLToPath(import.meta.url)),
  '../../../resources/META-INF/resources/static/scripts',
)

function loadProductionCatalogingCutter(): CatalogingCutterApi {
  const sandbox: Record<string, unknown> = {
    Core: {
      msg() {},
      popup() {},
    },
    Translations: {
      get: (key: string) => key,
    },
  }
  sandbox.window = sandbox
  sandbox.self = sandbox
  sandbox.globalThis = sandbox

  const context = createContext(sandbox)
  runInContext(
    readFileSync(resolve(scriptsDirectory, 'cutter-sanborn.min.js'), 'utf8'),
    context,
  )
  runInContext(
    `${readFileSync(resolve(scriptsDirectory, 'biblivre.cataloging.cutter.js'), 'utf8')}\nthis.CatalogingCutter = CatalogingCutter;`,
    context,
  )

  return sandbox.CatalogingCutter as CatalogingCutterApi
}

describe('biblivre.cataloging.cutter.js', () => {
  let CatalogingCutter: CatalogingCutterApi

  beforeAll(() => {
    CatalogingCutter = loadProductionCatalogingCutter()
  })

  it('parses MARC personal names', () => {
    expect(CatalogingCutter.parsePersonalName('Santos, Milton')).toEqual({
      surname: 'Santos',
      givenName: 'Milton',
    })
    expect(CatalogingCutter.parsePersonalName('Lentino')).toEqual({
      surname: 'Lentino',
      givenName: '',
    })
    expect(CatalogingCutter.parsePersonalName('')).toEqual({
      surname: '',
      givenName: '',
    })
  })

  it('parses corporate names using the first significant word', () => {
    expect(
      CatalogingCutter.parseCorporateName('Universidade Federal do Rio Grande'),
    ).toEqual({
      surname: 'Universidade',
      givenName: '',
    })
    expect(
      CatalogingCutter.parseCorporateName('A Biblioteca Nacional'),
    ).toEqual({
      surname: 'Biblioteca',
      givenName: '',
    })
  })

  it('derives work letter from title, skipping articles when ind2 is unset', () => {
    expect(CatalogingCutter.workLetter('A Natureza do Espaço')).toBe('n')
    expect(CatalogingCutter.workLetter('The Open Society')).toBe('o')
    expect(CatalogingCutter.workLetter('Natureza do Espaço')).toBe('n')
  })

  it('prefers MARC 245 indicator 2 non-filing characters', () => {
    expect(CatalogingCutter.workLetter('A Natureza do Espaço', 2)).toBe('n')
    expect(CatalogingCutter.workLetter('The Open Society', '4')).toBe('o')
    expect(CatalogingCutter.workLetter('XYZ Title', 0)).toBe('x')
  })

  it('builds Brazilian Cutter author codes with the vendored table', () => {
    const parts = CatalogingCutter.parsePersonalName('Santos, Milton')
    expect(
      CatalogingCutter.buildAuthorCode(
        parts.surname,
        parts.givenName,
        'A Natureza do Espaço',
        null,
      ),
    ).toBe('S237n')

    const lentino = CatalogingCutter.parsePersonalName('Lentino, Noemia')
    expect(
      CatalogingCutter.buildAuthorCode(
        lentino.surname,
        lentino.givenName,
        'Noite',
        null,
      ),
    ).toBe('L574n')
  })

  it('returns null when author, title, or table lookup is missing', () => {
    expect(CatalogingCutter.buildAuthorCode('', '', 'Title', null)).toBeNull()
    expect(
      CatalogingCutter.buildAuthorCode('Santos', 'Milton', '', null),
    ).toBeNull()
  })
})
