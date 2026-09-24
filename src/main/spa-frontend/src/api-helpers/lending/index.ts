import './registry'

import { fetchJSONFromLegacyEndpoint } from '..'

import type {
  LendingFineMutationResponse,
  LendingHoldingSearchResponse,
  LendingMutationResponse,
  LendingPrintReceiptResponse,
  LendingReturnImmediateResponse,
  LendingUndoReturnResponse,
  LendingUserSearchResponse,
} from './response-types'
import type {
  CreateLendingPayload,
  LendingHoldingSearchPayload,
  LendingUserSearchPayload,
} from './types'

export const searchHoldingsForReturn = async (
  payload: LendingHoldingSearchPayload,
): Promise<LendingHoldingSearchResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.lending',
    action: 'search',
    search_parameters: JSON.stringify({
      query: payload.query,
      holding_list_lendings: payload.holding_list_lendings ?? false,
    }),
  })
}

export const searchLendingUsers = async (
  payload: LendingUserSearchPayload,
): Promise<LendingUserSearchResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.lending',
    action: 'user_search',
    search_parameters: JSON.stringify({
      mode: 'simple',
      query: payload.query,
      field: payload.field ?? '',
    }),
    page: String(payload.page ?? 1),
  })
}

export const createLending = async ({
  holdingId,
  userId,
}: CreateLendingPayload): Promise<LendingMutationResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.lending',
    action: 'lend',
    holding_id: String(holdingId),
    user_id: String(userId),
  })
}

export const renewLending = async (
  lendingId: number,
): Promise<LendingMutationResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.lending',
    action: 'renew_lending',
    id: String(lendingId),
  })
}

export const returnLendingImmediate = async (
  lendingId: number,
): Promise<LendingReturnImmediateResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.lending',
    action: 'return_immediate',
    id: String(lendingId),
  })
}

export const undoLendingReturn = async (
  lendingId: number,
): Promise<LendingUndoReturnResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.lending',
    action: 'undo_return',
    id: String(lendingId),
  })
}

export const payLendingFine = async (
  fineId: number,
  exempt = false,
): Promise<LendingFineMutationResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.lending',
    action: 'pay_fine',
    fine_id: String(fineId),
    exempt: String(exempt),
  })
}

export const adjustLendingFine = async ({
  fineId,
  value,
}: {
  fineId: number
  value: number
}): Promise<LendingFineMutationResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.lending',
    action: 'adjust_fine',
    fine_id: String(fineId),
    value: String(value),
  })
}

export const printLendingReceipt = async (
  lendingIds: number[],
): Promise<LendingPrintReceiptResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.lending',
    action: 'print_receipt',
    id_list: lendingIds.join(','),
  })
}
