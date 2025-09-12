import { FIELDS } from '../../api-helpers/search/constants'

import type { Moment } from 'moment'

import type {
  AdvancedDateQueryTerm,
  AdvancedQueryTerm,
  AdvancedTextQueryTerm,
  BaseQueryTerm,
  ISO8601Date,
} from '../../api-helpers/search/types'

import type { DateRange, UUID } from './types'

function hasValidQuery([_, term]: [UUID, BaseQueryTerm]): boolean {
  return term.query !== ''
}

export function getValidQueries(
  termField: Map<UUID, AdvancedTextQueryTerm>,
  createdFilter: DateRange,
  modifiedFilter: DateRange
): AdvancedQueryTerm[] | undefined {
  const validTerms: AdvancedQueryTerm[] = [...termField.entries()]
    .filter(hasValidQuery)
    .map(([, term]) => term)

  if (createdFilter.from !== null || createdFilter.to !== null) {
    const createdTerm: AdvancedDateQueryTerm = {
      field: FIELDS.CREATED,
      operator: 'AND',
      start_date: getISO8601Date(createdFilter.from),
      end_date: getISO8601Date(createdFilter.to),
    }

    validTerms.push(createdTerm)
  }

  if (modifiedFilter.from !== null || modifiedFilter.to !== null) {
    const modifiedTerm: AdvancedDateQueryTerm = {
      field: FIELDS.MODIFIED,
      operator: 'AND',
      start_date: getISO8601Date(modifiedFilter.from),
      end_date: getISO8601Date(modifiedFilter.to),
    }

    validTerms.push(modifiedTerm)
  }

  if (validTerms.length > 0) {
    return validTerms
  }

  return undefined
}

const DUMMY_ADVANCED_QUERY_TERM: AdvancedTextQueryTerm = {
  field: FIELDS.ANY,
  operator: 'AND',
  query: '',
} as const

function getISO8601Date(date: Moment | null): ISO8601Date | null {
  if (date === null) {
    return null
  }

  return date
    .startOf('day')
    .toISOString()
    .slice(0, -'.000Z'.length) as ISO8601Date
}

export function generateTermField(): [UUID, AdvancedTextQueryTerm] {
  return [crypto.randomUUID(), DUMMY_ADVANCED_QUERY_TERM]
}
