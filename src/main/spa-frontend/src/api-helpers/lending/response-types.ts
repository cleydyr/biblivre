import type {
  HoldingLendingBag,
  LendingFine,
  ReservationInfo,
} from '../circulation/response-types'
import type {
  MaybeSuccessfulResponse,
  PaginatedResponsePayload,
} from '../types'

export type LendingHoldingSearchResponse =
  PaginatedResponsePayload<HoldingLendingBag>

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
