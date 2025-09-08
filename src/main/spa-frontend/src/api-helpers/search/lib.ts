import type {
  AdvancedQueryTerm,
  SearchMode,
  SearchQueryTerms,
  SimpleQueryTerm,
} from './types'

type SimpleSearchTerms = {
  search_terms: [SimpleQueryTerm]
}

type AdvancedSearchTerms = {
  search_terms: AdvancedQueryTerm[]
}

type SearchTerms = SimpleSearchTerms | AdvancedSearchTerms

export function isAdvancedQuery(
  terms: SearchQueryTerms
): terms is AdvancedQueryTerm[] {
  return Array.isArray(terms)
}

export function getSearchTerms(
  terms: SearchQueryTerms | undefined
): SearchTerms | undefined {
  if (terms === undefined) {
    return undefined
  }

  if (isAdvancedQuery(terms)) {
    return {
      search_terms: terms,
    }
  }

  return {
    search_terms: [terms],
  }
}

export function getSearchMode(query: SearchQueryTerms | undefined): SearchMode {
  if (query === undefined) {
    return 'list_all'
  }

  if (Array.isArray(query)) {
    return 'advanced'
  }

  return 'simple'
}
