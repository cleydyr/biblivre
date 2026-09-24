import { useMutation } from '@tanstack/react-query'

import {
  adjustLendingFine,
  createLending,
  payLendingFine,
  printLendingReceipt,
  renewLending,
  returnLendingImmediate,
  searchHoldingsForReturn,
  searchLendingUsers,
  undoLendingReturn,
} from '.'

import type {
  CreateLendingPayload,
  LendingHoldingSearchPayload,
  LendingUserSearchPayload,
} from './types'

export const useLendingHoldingSearchMutation = () => {
  return useMutation({
    mutationFn: (payload: LendingHoldingSearchPayload) =>
      searchHoldingsForReturn(payload),
  })
}

export const useLendingUserSearchMutation = () => {
  return useMutation({
    mutationFn: (payload: LendingUserSearchPayload) =>
      searchLendingUsers(payload),
  })
}

export const useCreateLendingMutation = () => {
  return useMutation({
    mutationFn: (payload: CreateLendingPayload) => createLending(payload),
  })
}

export const useRenewLendingMutation = () => {
  return useMutation({
    mutationFn: (lendingId: number) => renewLending(lendingId),
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
