import { fetchFromLegacyEndpoint } from '..'
import { MODULES } from '../constants'

import { ACTIONS } from './constants'
import type { SearchQuery } from './types'

export async function getCatalographicSearchResults(
  host: string,
  query: SearchQuery
) {
  const result = fetchFromLegacyEndpoint(
    host,
    'cataloging.bibliographic',
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
  host: string,
  search_id: string,
  page: number
) {
  const result = fetchFromLegacyEndpoint(
    host,
    MODULES.CATALOGING_BIBLIOGRAPHIC,
    ACTIONS.PAGINATE,
    {
      page: String(page),
      search_id,
    }
  )

  return result
}

export async function openBibliographicRecord(host: string, recordId: number) {
  return await fetchFromLegacyEndpoint(
    host,
    MODULES.CATALOGING_BIBLIOGRAPHIC,
    ACTIONS.OPEN,
    {
      id: String(recordId),
    }
  )
}
