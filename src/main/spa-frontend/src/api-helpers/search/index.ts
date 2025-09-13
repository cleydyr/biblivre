import { MODULES } from '../constants'
import { fetchFromLegacyEndpoint } from '..'

import { ACTIONS, FIELDS } from './constants'
import { getSearchMode, getSearchTerms } from './lib'

import type {
  BibliographicMaterial,
  SearchQueryTerms,
  SearchResponse,
} from './types'

export async function getCatalographicSearchResults(
  materialType: BibliographicMaterial,
  terms?: SearchQueryTerms
): Promise<SearchResponse> {
  return fetchFromLegacyEndpoint({
    module: MODULES.CATALOGING_BIBLIOGRAPHIC,
    action: ACTIONS.SEARCH,
    search_parameters: JSON.stringify({
      database: 'main',
      material_type: materialType,
      search_mode: getSearchMode(terms),
      ...getSearchTerms(terms),
    }),
  })
}

export async function paginateCatalographicSearchResults(
  search_id: string,
  page: number,
  sort?: number
): Promise<SearchResponse> {
  const result = fetchFromLegacyEndpoint({
    module: MODULES.CATALOGING_BIBLIOGRAPHIC,
    action: ACTIONS.PAGINATE,
    page: String(page + 1),
    search_id,
    sort: sort ? String(sort) : FIELDS.TITLE,
  })

  return result
}

export async function openBibliographicRecord(recordId: string) {
  return await fetchFromLegacyEndpoint({
    module: MODULES.CATALOGING_BIBLIOGRAPHIC,
    action: ACTIONS.OPEN,
    id: recordId,
  })
}
