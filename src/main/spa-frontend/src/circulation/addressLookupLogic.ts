import type { AddressLookupResult } from '../generated-sources'

export const ADDRESS_LOOKUP_FIELD_KEYS = [
  'address',
  'address_neighborhood',
  'address_city',
  'address_state',
] as const

export type AddressLookupFieldKey = (typeof ADDRESS_LOOKUP_FIELD_KEYS)[number]

export function normalizeCep(rawCep: string): string {
  return rawCep.replaceAll(/\D/g, '')
}

export function isValidBrazilianCep(rawCep: string): boolean {
  return /^\d{8}$/.test(normalizeCep(rawCep))
}

export function addressLookupToFieldValues(
  result: AddressLookupResult,
): Partial<Record<AddressLookupFieldKey, string>> {
  const values: Partial<Record<AddressLookupFieldKey, string>> = {}

  if (result.street) {
    values.address = result.street
  }
  if (result.neighborhood) {
    values.address_neighborhood = result.neighborhood
  }
  if (result.city) {
    values.address_city = result.city
  }
  if (result.state) {
    values.address_state = result.state
  }

  return values
}

/**
 * True when the lookup would write into at least one mapped field that already
 * has a value — staff must confirm before replacing filled address data.
 */
export function hasAddressFieldsToOverwrite(
  currentFields: Record<string, string>,
  lookupValues: Partial<Record<AddressLookupFieldKey, string>>,
): boolean {
  return ADDRESS_LOOKUP_FIELD_KEYS.some((key) => {
    const lookupValue = lookupValues[key]
    if (!lookupValue) {
      return false
    }

    return (currentFields[key] ?? '').trim().length > 0
  })
}

export function applyAddressLookupValues(
  currentFields: Record<string, string>,
  lookupValues: Partial<Record<AddressLookupFieldKey, string>>,
): Record<string, string> {
  return {
    ...currentFields,
    ...lookupValues,
  }
}
