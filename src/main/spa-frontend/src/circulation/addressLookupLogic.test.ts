import { describe, expect, it } from 'vitest'

import {
  addressLookupToFieldValues,
  applyAddressLookupValues,
  hasAddressFieldsToOverwrite,
  isValidBrazilianCep,
  normalizeCep,
} from './addressLookupLogic'

describe('addressLookupLogic', () => {
  it('normalizes and validates Brazilian CEPs', () => {
    expect(normalizeCep('01310-100')).toBe('01310100')
    expect(isValidBrazilianCep('01310-100')).toBe(true)
    expect(isValidBrazilianCep('1310100')).toBe(false)
  })

  it('maps only non-empty lookup fields', () => {
    expect(
      addressLookupToFieldValues({
        street: '',
        neighborhood: 'Centro',
        city: 'São Paulo',
        state: 'SP',
        incomplete: true,
      }),
    ).toEqual({
      address_neighborhood: 'Centro',
      address_city: 'São Paulo',
      address_state: 'SP',
    })
  })

  it('detects overwrite when any mapped field already has a value the lookup would write', () => {
    expect(
      hasAddressFieldsToOverwrite(
        { address: 'Rua A', address_city: '' },
        { address: 'Rua B', address_city: 'Campinas' },
      ),
    ).toBe(true)

    expect(
      hasAddressFieldsToOverwrite(
        { address: 'Rua A' },
        { address: 'Rua A' },
      ),
    ).toBe(true)

    expect(
      hasAddressFieldsToOverwrite(
        { address: '', address_city: '' },
        { address: 'Rua B', address_city: 'Campinas' },
      ),
    ).toBe(false)
  })

  it('applies lookup values without clearing omitted fields', () => {
    expect(
      applyAddressLookupValues(
        { address: 'Rua antiga', address_city: 'X', address_number: '10' },
        { address_city: 'Y' },
      ),
    ).toEqual({
      address: 'Rua antiga',
      address_city: 'Y',
      address_number: '10',
    })
  })
})
