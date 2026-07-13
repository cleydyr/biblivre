import type { DateRange } from '../types'

export function getEmptyDateRange(): DateRange {
  return {
    from: null,
    to: null,
  }
}
