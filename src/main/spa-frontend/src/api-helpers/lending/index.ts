import './registry'

import { fetchJSONFromLegacyEndpoint } from '..'

import type {
  LendingFineMutationResponse,
  LendingHoldingSearchResponse,
  LendingPrintReceiptResponse,
  LendingReturnImmediateResponse,
  LendingUndoReturnResponse,
} from './response-types'
import type { LendingHoldingSearchPayload } from './types'

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
