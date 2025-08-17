import { MODULES } from '../constants'
import { fetchFromLegacyEndpoint } from '..'

import { ACTIONS } from './constants'

import type { SearchQuery, SearchResponse } from './types'

export async function getCatalographicSearchResults(
  query?: SearchQuery
): Promise<SearchResponse> {
  const result = fetchFromLegacyEndpoint(
    MODULES.CATALOGING_BIBLIOGRAPHIC,
    ACTIONS.SEARCH,
    {
      search_parameters: JSON.stringify({
        database: 'main',
        material_type: 'all',
        search_mode: query ? 'simple' : 'list_all',
        search_terms: query ? [{ query }] : undefined,
      }),
    }
  )

  return result
}

export async function paginateCatalographicSearchResults(
  search_id: string,
  page: number
): Promise<SearchResponse> {
  const result = fetchFromLegacyEndpoint(
    MODULES.CATALOGING_BIBLIOGRAPHIC,
    ACTIONS.PAGINATE,
    {
      page: String(page),
      search_id,
    }
  )

  return result
}

export async function openBibliographicRecord(recordId: string) {
  return await fetchFromLegacyEndpoint(
    MODULES.CATALOGING_BIBLIOGRAPHIC,
    ACTIONS.OPEN,
    {
      id: recordId,
    }
  )
}
