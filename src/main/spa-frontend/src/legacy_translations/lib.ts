import translations from './pt-br.json'

import type {
  Digit,
  FieldIndicatorIndex,
  MarcDatafield,
  MarcField,
  MarcSubfield,
} from '../api-helpers/search/response-types'

export function getLegacyTranslation(key: keyof typeof translations) {
  return translations[key]
}

export function getLegacyMarcDatafieldTranslation(field: MarcField) {
  const customKey = `cataloging.tab.record.custom.field_label.biblio_${field}`

  if (customKey in translations) {
    return translations[customKey as keyof typeof translations]
  }

  const key = `marc.bibliographic.datafield.${field}`

  if (key in translations) {
    return translations[key as keyof typeof translations]
  }

  return 'No translation found'
}

export function getLegacySubfieldTranslation(
  datafield: MarcDatafield,
  subfield: MarcSubfield,
) {
  const key = `marc.bibliographic.datafield.${datafield}.subfield.${subfield}`

  if (key in translations) {
    return translations[key as keyof typeof translations]
  }

  return 'No translation found'
}

export function getLegacyIndicatorTranslation(
  datafield: MarcDatafield,
  indicator: FieldIndicatorIndex,
) {
  const key = `marc.bibliographic.datafield.${datafield}.indicator.${indicator}`

  if (key in translations) {
    return translations[key as keyof typeof translations]
  }

  return 'No translation found'
}

export function getLegacyIndicatorValueTranslation(
  datafield: MarcDatafield,
  indicator: FieldIndicatorIndex,
  value: Digit,
) {
  const key = `marc.bibliographic.datafield.${datafield}.indicator.${indicator}.${value}`

  if (key in translations) {
    return translations[key as keyof typeof translations]
  }

  return 'No translation found'
}
