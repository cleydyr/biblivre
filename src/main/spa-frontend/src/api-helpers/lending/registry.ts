import type { ParametrizedLegacyEndpointPayload } from '../types'

type LendingSearchPayload = ParametrizedLegacyEndpointPayload<
  'circulation.lending',
  'search',
  'search_parameters'
>

type LendingUserSearchPayload = ParametrizedLegacyEndpointPayload<
  'circulation.lending',
  'user_search',
  'search_parameters',
  'page'
>

type LendingCreatePayload = ParametrizedLegacyEndpointPayload<
  'circulation.lending',
  'lend',
  'holding_id' | 'user_id'
>

type LendingRenewPayload = ParametrizedLegacyEndpointPayload<
  'circulation.lending',
  'renew_lending',
  'id'
>

type LendingReturnImmediatePayload = ParametrizedLegacyEndpointPayload<
  'circulation.lending',
  'return_immediate',
  'id'
>

type LendingUndoReturnPayload = ParametrizedLegacyEndpointPayload<
  'circulation.lending',
  'undo_return',
  'id'
>

type LendingPayFinePayload = ParametrizedLegacyEndpointPayload<
  'circulation.lending',
  'pay_fine',
  'fine_id',
  'exempt'
>

type LendingAdjustFinePayload = ParametrizedLegacyEndpointPayload<
  'circulation.lending',
  'adjust_fine',
  'fine_id' | 'value'
>

type LendingPrintReceiptPayload = ParametrizedLegacyEndpointPayload<
  'circulation.lending',
  'print_receipt',
  'id_list'
>

declare module '../../registry' {
  interface ParametrizedLegacyEndpointPayloadRegistry {
    LendingSearchPayload: LendingSearchPayload
    LendingUserSearchPayload: LendingUserSearchPayload
    LendingCreatePayload: LendingCreatePayload
    LendingRenewPayload: LendingRenewPayload
    LendingReturnImmediatePayload: LendingReturnImmediatePayload
    LendingUndoReturnPayload: LendingUndoReturnPayload
    LendingPayFinePayload: LendingPayFinePayload
    LendingAdjustFinePayload: LendingAdjustFinePayload
    LendingPrintReceiptPayload: LendingPrintReceiptPayload
  }
}
