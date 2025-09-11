import { FIELDS } from '../../api-helpers/search/constants'

import type {
  AdvancedQueryTerm,
  BaseQueryTerm,
} from '../../api-helpers/search/types'

import type { UUID } from './types'

function hasValidQuery([_, term]: [UUID, BaseQueryTerm]): boolean {
  return term.query !== ''
}

export function getValidQueries(
  termField: Map<UUID, AdvancedQueryTerm>
): AdvancedQueryTerm[] | undefined {
  const validQueries = [...termField.entries()]
    .filter(hasValidQuery)
    .map(([, term]) => term)

  if (validQueries.length > 0) {
    return validQueries
  }

  return undefined
}

const DUMMY_ADVANCED_QUERY_TERM: AdvancedQueryTerm = {
  field: FIELDS.ANY,
  operator: 'AND',
  query: '',
} as const

export function generateTermField(): [UUID, AdvancedQueryTerm] {
  return [crypto.randomUUID(), DUMMY_ADVANCED_QUERY_TERM]
}
