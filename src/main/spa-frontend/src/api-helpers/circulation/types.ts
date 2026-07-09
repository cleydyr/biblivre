import type { ISO8601Date } from '../../types'

import type { User } from './response-types'

type BaseCirculationUserSearchPayload = { query: string; field: string }

type CirculationSimpleUserSearchPayload = BaseCirculationUserSearchPayload & {
  mode: 'simple'
}

type CirculationAdvancedUserSearchPayload = BaseCirculationUserSearchPayload & {
  mode: 'advanced'
  created_start: ISO8601Date | ''
  created_end: ISO8601Date | ''
  modified_start: ISO8601Date | ''
  modified_end: ISO8601Date | ''
  users_with_pending_fines: boolean
  users_with_late_lendings: boolean
  users_who_have_login_access: boolean
  users_without_user_card: boolean
  inactive_users_only: boolean
}

export type CirculationSearchPayload =
  | CirculationSimpleUserSearchPayload
  | CirculationAdvancedUserSearchPayload

export type CirculationPaginatePayload = CirculationSearchPayload & {
  page: number
}

export type CirculationUserTab = 'lendings' | 'reservations' | 'fines'

export type CirculationUserSavePayload = {
  id: number
  name: string
  type: number
  status: User['status']
  photo_data?: string
} & Record<string, string | number>
