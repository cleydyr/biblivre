import { useMutation } from '@tanstack/react-query'

import {
  adjustLendingFine,
  lendHolding,
  payLendingFine,
  printLendingReceipt,
  renewLending,
  returnLendingImmediate,
  searchHoldingsForReturn,
  searchReadersForLending,
  undoLendingReturn,
} from '.'

import type {
  LendingHoldingSearchPayload,
  LendingUserSearchPayload,
} from './types'

export const useLendingHoldingSearchMutation = () => {
  return useMutation({
    mutationFn: (payload: LendingHoldingSearchPayload) =>
      searchHoldingsForReturn(payload),
  })
}

export const useReturnLendingImmediateMutation = () => {
  return useMutation({
    mutationFn: (lendingId: number) => returnLendingImmediate(lendingId),
  })
}

export const useUndoLendingReturnMutation = () => {
  return useMutation({
    mutationFn: (lendingId: number) => undoLendingReturn(lendingId),
  })
}

export const usePayLendingFineMutation = () => {
  return useMutation({
    mutationFn: ({ fineId, exempt }: { fineId: number; exempt?: boolean }) =>
      payLendingFine(fineId, exempt ?? false),
  })
}

export const useAdjustLendingFineMutation = () => {
  return useMutation({
    mutationFn: adjustLendingFine,
  })
}

export const usePrintLendingReceiptMutation = () => {
  return useMutation({
    mutationFn: (lendingIds: number[]) => printLendingReceipt(lendingIds),
  })
}

export const useLendingReaderSearchMutation = () => {
  return useMutation({
    mutationFn: (payload: LendingUserSearchPayload) =>
      searchReadersForLending(payload),
  })
}

export const useLendHoldingMutation = () => {
  return useMutation({
    mutationFn: (payload: Parameters<typeof lendHolding>[0]) =>
      lendHolding(payload),
  })
}

export const useRenewLendingMutation = () => {
  return useMutation({
    mutationFn: (lendingId: number) => renewLending(lendingId),
  })
}
