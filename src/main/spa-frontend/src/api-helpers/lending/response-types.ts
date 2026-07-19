import type {
  HoldingLendingBag,
  LendingFine,
  ReservationInfo,
} from '../circulation/response-types'
import type {
  NonSuccessfulResponse,
  PaginatedResponsePayload,
  SuccessfulResponse,
} from '../types'

export type LendingHoldingSearchResponse =
  PaginatedResponsePayload<HoldingLendingBag>

// TODO: extract successful<T> | NonSuccessfulResponse to a generic type
export type LendingReturnImmediateResponse =
  | (SuccessfulResponse & {
      data: HoldingLendingBag
      full_data?: boolean
      undo_window_seconds: number
      undo_available_until?: number
      next_reservation?: ReservationInfo
      message?: string
    })
  | NonSuccessfulResponse

export type LendingUndoReturnResponse =
  | (SuccessfulResponse & {
      data: HoldingLendingBag
      full_data?: boolean
      message?: string
    })
  | NonSuccessfulResponse

export type LendingFineMutationResponse =
  | (SuccessfulResponse & {
      data?: LendingFine
      message?: string
    })
  | NonSuccessfulResponse

export type LendingPrintReceiptResponse =
  | (SuccessfulResponse & {
      receipt: string
      message?: string
    })
  | NonSuccessfulResponse
