import type { FIELDS } from './constants'

export type BaseQueryTerm = {
  query: string
}

export type SimpleQueryTerm = BaseQueryTerm

export type EncodedQueryField = (typeof FIELDS)[keyof typeof FIELDS]

export type EncodedTextQueryField = Exclude<
  EncodedQueryField,
  DateTermQueryField
>

export type QueryOperator = 'AND' | 'OR' | 'AND_NOT'

export type ISO8601Date = string & { __brand: 'iso8601' }

type DateTermQueryField = typeof FIELDS.MODIFIED | typeof FIELDS.CREATED

export type AdvancedTextQueryTerm = BaseQueryTerm & {
  operator: QueryOperator
  field: EncodedTextQueryField
}

export type AdvancedDateQueryTerm = {
  operator: 'AND'
  field: DateTermQueryField
  start_date: ISO8601Date | null
  end_date: ISO8601Date | null
}

export type AdvancedQueryTerm = AdvancedTextQueryTerm | AdvancedDateQueryTerm

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
