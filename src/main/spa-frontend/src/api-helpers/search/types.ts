import type { FIELDS } from './constants'

export type SimpleQueryTerm = {
  query: string
}

export type EncodedQueryField = (typeof FIELDS)[keyof typeof FIELDS]

export type QueryOperator = 'AND' | 'OR' | 'AND_NOT'

export type AdvancedQueryTerm = SimpleQueryTerm & {
  operator: QueryOperator
  field: EncodedQueryField
}

export type SearchQueryTerms = SimpleQueryTerm | AdvancedQueryTerm[]

export type SearchMode = 'list_all' | 'simple' | 'advanced'

// Re-export response types
export type {
  BibliographicRecord,
  IndexingGroup,
  IndexingGroupCount,
  MarcField,
  SearchResponse,
} from './response-types'
