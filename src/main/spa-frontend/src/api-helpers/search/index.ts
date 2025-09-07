import { MODULES } from '../constants'
import { fetchFromLegacyEndpoint } from '..'

import { ACTIONS } from './constants'
import { getSearchMode, getSearchTerms } from './lib'

import type { SearchQuery, SearchResponse } from './types'

export async function getCatalographicSearchResults(
  queries?: SearchQuery
): Promise<SearchResponse> {
  return fetchFromLegacyEndpoint({
    module: MODULES.CATALOGING_BIBLIOGRAPHIC,
    action: ACTIONS.SEARCH,
    search_parameters: JSON.stringify({
      database: 'main',
      material_type: 'all',
      search_mode: getSearchMode(queries),
      ...getSearchTerms(queries),
    }),
  })
}

export async function paginateCatalographicSearchResults(
  search_id: string,
  page: number
): Promise<SearchResponse> {
  const result = fetchFromLegacyEndpoint({
    module: MODULES.CATALOGING_BIBLIOGRAPHIC,
    action: ACTIONS.PAGINATE,
    page: String(page + 1),
    search_id,
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
