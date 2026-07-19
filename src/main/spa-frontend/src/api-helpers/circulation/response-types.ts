import type { Branded, ISO8601Date } from '../../types'
import type { BibliographicRecord, Holding } from '../search/response-types'
import type {
  Auditable,
  NonSuccessfulResponse,
  PaginatedResponsePayload,
  SuccessfulResponse,
} from '../types'

import type { CirculationUserTab } from './types'

export type User = Auditable<{
  type_name: string
  loginId: number
  name: string
  id: number
  photo_id?: string
  type: number
  fields: {
    birthday: ISO8601Date
    obs: string
    address: string
    gender: string
    address_complement: string
    phone_work: string
    address_state: string
    id_rg: string
    id_cpf: string
    address_number: string
    address_city: string
    phone_home: string
    phone_cel: string
    phone_work_extension: string
    address_zip: string
    email: string
  }
  enrollment: string
  status: 'active' | 'inactive' | 'blocked' | 'pending_issues'
}>

export type CirculationUsersSearchResponse = PaginatedResponsePayload<User>

export type CirculationUserStatusChangeResponse =
  | (SuccessfulResponse & {
      message?: string
      message_level?: string
    })
  | NonSuccessfulResponse

export type CirculationUserFieldErrors = Record<string, string>

export type CirculationUserSaveResponse =
  | (SuccessfulResponse & {
      data: User
      message?: string
      message_level?: string
      full_data?: boolean
    })
  | (NonSuccessfulResponse & {
      errors?: CirculationUserFieldErrors[]
    })

export type CirculationUserTabDataResponse<T extends CirculationUserTab> =
  SuccessfulResponse & {
    data: {
      data?: CirculationUserTabData<T>[]
    }
  }

export type LendingFine = Auditable<{
  id: number
  lendingId: number
  userId: number
  value: number
  payment: ISO8601Date
  title: string
  author: string
}>

export type Lending = Auditable<{
  expectedReturnDate: ISO8601Date
  previousLendingId: number
  id: number
  userId: number
  holdingId: number
  returnDate?: ISO8601Date
  daysLate?: number
  estimatedFine?: number
}>

export type LendingBag = Auditable<{
  id: number
  biblio: BibliographicRecord
  holding: Holding
  user: User
  lending: Lending
  lendingFine: LendingFine
}>

/** Holding-first search / Return bag: open or last closed Lending may be absent. */
export type HoldingLendingBag = Auditable<{
  id: number
  biblio?: BibliographicRecord
  holding: Holding
  user?: User
  lending?: Lending
  lendingFine?: LendingFine
}>

export type Reservation = Auditable<{
  recordId: number
  expires: ISO8601Date
  id: number
  userId: number
}>

export type ReservationInfo = Auditable<{
  biblio?: BibliographicRecord
  reservation: Reservation
  user?: User
}>

export type ReservationBag = Auditable<{
  biblio: BibliographicRecord
  reservation: Reservation
  user?: User
}>

// Unfortunate name: the real reservation list is the list of reservation bags
export type ReservationList = Auditable<{
  id: number
  user: User
  reservationInfoList: ReservationBag[]
}>

export type CirculationUserTabData<T extends CirculationUserTab> = Omit<
  Extract<
    | Branded<ReservationList, 'reservations'>
    | Branded<LendingBag, 'lendings'>
    | Branded<LendingFine, 'fines'>,
    {
      __brand: T
    }
  >,
  '__brand'
>
