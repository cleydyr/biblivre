import type {
  AdvancedQuery,
  SearchMode,
  SearchQuery,
  SearchTerms,
} from './types'

export function isAdvancedQuery(query: SearchQuery): query is AdvancedQuery[] {
  return Array.isArray(query)
}

export function getSearchTerms(
  query: SearchQuery | undefined
): SearchTerms | undefined {
  if (query === undefined) {
    return undefined
  }

  if (isAdvancedQuery(query)) {
    return {
      search_terms: query,
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
