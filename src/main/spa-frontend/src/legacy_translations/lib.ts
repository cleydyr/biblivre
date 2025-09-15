import translations from './pt-br.json'

import type { MarcField } from '../api-helpers/search/response-types'

export function getLegacyTranslation(key: keyof typeof translations) {
  return translations[key]
}

export function getLegacyMarcDatafieldTranslation(field: MarcField) {
  const key = `cataloging.tab.record.custom.field_label.biblio_${field}`

  if (key in translations) {
    return translations[key as keyof typeof translations]
  }

  return field
}
