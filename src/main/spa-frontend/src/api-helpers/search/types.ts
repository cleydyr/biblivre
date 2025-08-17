import type { SEARCHABLE_FIELDS } from './constants'

export type SimpleQuery = {
  query: string
}

export type QueryField = (typeof SEARCHABLE_FIELDS)[number]

export type QueryOperator = 'AND' | 'OR' | 'AND_NOT'

export type AdvancedQuery = SimpleQuery & {
  operator: QueryOperator
  field: QueryField
}

export type SearchQuery = SimpleQuery | AdvancedQuery
