import { useMutation } from '@tanstack/react-query'

import {
  adjustLendingFine,
  payLendingFine,
  printLendingReceipt,
  returnLendingImmediate,
  searchHoldingsForReturn,
  undoLendingReturn,
} from '.'

import type { LendingHoldingSearchPayload } from './types'

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
