import type {
  HoldingLendingBag,
  LendingFine,
  ReservationInfo,
  User,
} from '../circulation/response-types'
import type {
  MaybeSuccessfulResponse,
  PaginatedResponsePayload,
} from '../types'

export type LendingHoldingSearchResponse =
  PaginatedResponsePayload<HoldingLendingBag>

/**
 * `circulation.lending` `user_search` row (Java `LendingListDTO`). The legacy
 * serializer omits empty lists, so both arrays may be absent.
 */
export type LendingReader = {
  id: number
  user: User
  /** Open lendings, with `daysLate`/`estimatedFine` set when late */
  lendingInfo?: HoldingLendingBag[]
  reservedRecords?: number[]
}

export type LendingUserSearchResponse = PaginatedResponsePayload<LendingReader>

/** `lend` and `renew_lending` answer with the holding's current lending. */
export type LendingMutationResponse = MaybeSuccessfulResponse<{
  data: HoldingLendingBag
  full_data?: boolean
  message?: string
}>

export type LendingReturnImmediateResponse = MaybeSuccessfulResponse<{
  data: HoldingLendingBag
  full_data?: boolean
  undo_window_seconds: number
  undo_available_until?: number
  next_reservation?: ReservationInfo
  message?: string
}>

export type LendingUndoReturnResponse = MaybeSuccessfulResponse<{
  data: HoldingLendingBag
  full_data?: boolean
  message?: string
}>

export type LendingFineMutationResponse = MaybeSuccessfulResponse<{
  data?: LendingFine
  message?: string
}>

export type LendingPrintReceiptResponse = MaybeSuccessfulResponse<{
  receipt: string
  message?: string
}>
