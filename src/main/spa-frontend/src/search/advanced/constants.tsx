import { FIELDS } from '../../api-helpers/search/constants'

import type { AdvancedQueryTerm } from '../../api-helpers/search/types'

export const DUMMY_ADVANCED_QUERY_TERM: AdvancedQueryTerm = {
  field: FIELDS.ANY,
  operator: 'AND',
  query: '',
} as const
