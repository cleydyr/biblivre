import type { DateRange } from '../types'

export type CirculationUsersAdvancedSearchFilters = {
  createdAtRange: DateRange
  modifiedAtRange: DateRange
  usersWithPendingFines: boolean
  usersWithLateLendings: boolean
  usersWhoHaveLoginAccess: boolean
  usersWithoutUserCard: boolean
  inactiveUsersOnly: boolean
}

type BaseCirculationSearchControlConfig = {
  query: string
  searchField: string
}

type CirculationSimpleSearchControlConfig =
  BaseCirculationSearchControlConfig & {
    isAdvancedSearch: false
  }

type CirculationAdvancedSearchControlConfig =
  BaseCirculationSearchControlConfig & {
    isAdvancedSearch: true
    filters: CirculationUsersAdvancedSearchFilters
  }

export type CirculationSearchControlConfig =
  CirculationSimpleSearchControlConfig | CirculationAdvancedSearchControlConfig
