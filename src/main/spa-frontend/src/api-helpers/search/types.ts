import type { FIELDS } from './constants'

export type SimpleQuery = {
  query: string
}

export type EncodedQueryField = (typeof FIELDS)[keyof typeof FIELDS]

export type QueryOperator = 'AND' | 'OR' | 'AND_NOT'

export type AdvancedQuery = SimpleQuery & {
  operator: QueryOperator
  field: EncodedQueryField
}

export type SearchQuery = SimpleQuery | AdvancedQuery[]

export type SearchMode = 'list_all' | 'simple' | 'advanced'

type SimpleSearchTerms = {
  search_terms: [SimpleQuery]
}

type AdvancedSearchTerms = {
  search_terms: AdvancedQuery[]
}

export type SearchTerms = SimpleSearchTerms | AdvancedSearchTerms

// Re-export response types
export type {
  BibliographicRecord,
  IndexingGroup,
  IndexingGroupCount,
  MarcField,
  SearchResponse,
} from './response-types'
