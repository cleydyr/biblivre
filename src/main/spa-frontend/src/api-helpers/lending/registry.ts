import type { ParametrizedLegacyEndpointPayload } from '../types'

type LendingSearchPayload = ParametrizedLegacyEndpointPayload<
  'circulation.lending',
  'search',
  'search_parameters'
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
    LendingReturnImmediatePayload: LendingReturnImmediatePayload
    LendingUndoReturnPayload: LendingUndoReturnPayload
    LendingPayFinePayload: LendingPayFinePayload
    LendingAdjustFinePayload: LendingAdjustFinePayload
    LendingPrintReceiptPayload: LendingPrintReceiptPayload
  }
}
