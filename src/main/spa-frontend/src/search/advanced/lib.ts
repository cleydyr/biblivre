import type {
  AdvancedQueryTerm,
  BaseQueryTerm,
} from '../../api-helpers/search/types'

import type { UUID } from './AdvancedBibliographicSearchControls'

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
