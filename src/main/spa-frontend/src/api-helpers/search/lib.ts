import { FIELDS } from './constants'

import type {
  AdvancedQuery,
  EncodedAdvancedQuery,
  SearchMode,
  SearchQuery,
  SearchTerms,
} from './types'

export function isAdvancedQuery(query: SearchQuery): query is AdvancedQuery[] {
  return Array.isArray(query)
}

export function adaptAdvancedQuery(query: AdvancedQuery): EncodedAdvancedQuery {
  return {
    ...query,
    field: FIELDS[query.field],
  }
}

export function getSearchTerms(
  query: SearchQuery | undefined
): SearchTerms | undefined {
  if (query === undefined) {
    return undefined
  }

  if (isAdvancedQuery(query)) {
    return {
      search_terms: query.map(adaptAdvancedQuery),
    }
  }

  return {
    search_terms: [query],
  }
}

export function getSearchMode(query: SearchQuery | undefined): SearchMode {
  if (query === undefined) {
    return 'list_all'
  }

  if (Array.isArray(query)) {
    return 'advanced'
  }

  return 'simple'
}
