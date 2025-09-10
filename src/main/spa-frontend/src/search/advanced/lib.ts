import type { AdvancedQueryTerm } from '../../api-helpers/search/types'

import type { UUID } from './AdvancedBibliographicSearchControls'

export function hasValidQuery([_, term]: [UUID, AdvancedQueryTerm]): boolean {
  return term.query !== ''
}
