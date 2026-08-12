import { describe, expect, it } from 'vitest'

import {
  buildAuthorCode,
  parseCorporateName,
  parsePersonalName,
  workLetter,
} from './cutterLogic'

describe('cutterLogic', () => {
  const callNumber = (surname: string, givenName: string) => {
    if (surname === 'santos' && givenName === 'milton') {
      return 237
    }
    if (surname === 'lentino' && givenName === 'noemia') {
      return 574
    }
    return -1
  }

  it('parses MARC personal names', () => {
    expect(parsePersonalName('Santos, Milton')).toEqual({
      surname: 'Santos',
      givenName: 'Milton',
    })
    expect(parsePersonalName('Lentino')).toEqual({
      surname: 'Lentino',
      givenName: '',
    })
    expect(parsePersonalName('')).toEqual({ surname: '', givenName: '' })
  })

  it('parses corporate names using the first significant word', () => {
    expect(parseCorporateName('Universidade Federal do Rio Grande')).toEqual({
      surname: 'Universidade',
      givenName: '',
    })
    expect(parseCorporateName('A Biblioteca Nacional')).toEqual({
      surname: 'Biblioteca',
      givenName: '',
    })
  })

  it('derives work letter from title, skipping articles when ind2 is unset', () => {
    expect(workLetter('A Natureza do Espaço')).toBe('n')
    expect(workLetter('The Open Society')).toBe('o')
    expect(workLetter('Natureza do Espaço')).toBe('n')
  })

  it('prefers MARC 245 indicator 2 non-filing characters', () => {
    expect(workLetter('A Natureza do Espaço', 2)).toBe('n')
    expect(workLetter('The Open Society', '4')).toBe('o')
    expect(workLetter('XYZ Title', 0)).toBe('x')
  })

  it('builds Brazilian Cutter author codes from surname, number, and work letter', () => {
    const parts = parsePersonalName('Santos, Milton')
    expect(
      buildAuthorCode(
        parts.surname,
        parts.givenName,
        'A Natureza do Espaço',
        null,
        callNumber,
      ),
    ).toBe('S237n')

    const lentino = parsePersonalName('Lentino, Noemia')
    expect(
      buildAuthorCode(
        lentino.surname,
        lentino.givenName,
        'Noite',
        null,
        callNumber,
      ),
    ).toBe('L574n')
  })

  it('returns null when author, title, or table lookup is missing', () => {
    expect(buildAuthorCode('', '', 'Title', null, callNumber)).toBeNull()
    expect(buildAuthorCode('Santos', 'Milton', '', null, callNumber)).toBeNull()
    expect(
      buildAuthorCode('Santos', 'Milton', 'Title', null, () => -1),
    ).toBeNull()
  })
})
