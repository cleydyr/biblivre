import { useCallback, useEffect, useRef, useState } from 'react'
import { useIntl } from 'react-intl'

import {
  useAdjustLendingFineMutation,
  useLendingHoldingSearchMutation,
  usePayLendingFineMutation,
  usePrintLendingReceiptMutation,
  useReturnLendingImmediateMutation,
  useUndoLendingReturnMutation,
} from '../../api-helpers/lending/hooks'
import { useToasts } from '../../toasts/useToasts'

import type {
  HoldingLendingBag,
  ReservationInfo,
} from '../../api-helpers/circulation/response-types'

export type ReturnSlipItem = {
  lendingId: number
  bag: HoldingLendingBag
  returnedAt: number
}

export type LastReturnState = {
  holdingLendingBag: HoldingLendingBag
  undoAvailableUntil?: number
  nextReservation?: ReservationInfo
}

/**
 * Auto-Return only when the query uniquely identifies a Holding by accession
 * number or holding id — not by title/author hits.
 */
export function findUniqueHoldingIdentifierMatch(
  query: string,
  holdingLendingBags: HoldingLendingBag[],
): HoldingLendingBag | null {
  const normalizedQuery = query.trim()
  if (!normalizedQuery || holdingLendingBags.length === 0) {
    return null
  }

  const byAccession = holdingLendingBags.filter(
    (bag) =>
      bag.holding.accession_number.toLowerCase() ===
      normalizedQuery.toLowerCase(),
  )
  if (byAccession.length === 1) {
    return byAccession[0]
  }

  if (isHoldingId(normalizedQuery)) {
    const holdingId = Number(normalizedQuery)
    const holdingsWithGivenId = holdingLendingBags.filter(
      (bag) => bag.holding.id === holdingId,
    )
    if (holdingsWithGivenId.length === 1) {
      return holdingsWithGivenId[0]
    }
  }

  return null
}

const isHoldingId = (value: string) => /^\d+$/.test(value)

const useCirculationReturn = () => {
  const { formatMessage } = useIntl()
  const { showToast } = useToasts()
  const searchInputRef = useRef<HTMLInputElement>(null)

  const [query, setQuery] = useState('')
  const [results, setResults] = useState<HoldingLendingBag[] | null>(null)
  const [slip, setSlip] = useState<ReturnSlipItem[]>([])
  const [lastReturn, setLastReturn] = useState<LastReturnState | null>(null)
  const [adjustFineId, setAdjustFineId] = useState<number | null>(null)
  const [adjustValue, setAdjustValue] = useState('')

  const searchMutation = useLendingHoldingSearchMutation()
  const returnMutation = useReturnLendingImmediateMutation()
  const undoMutation = useUndoLendingReturnMutation()
  const payFineMutation = usePayLendingFineMutation()
  const adjustFineMutation = useAdjustLendingFineMutation()
  const printReceiptMutation = usePrintLendingReceiptMutation()

  const focusSearch = useCallback(() => {
    searchInputRef.current?.focus()
    searchInputRef.current?.select()
  }, [])

  useEffect(() => {
    focusSearch()
  }, [focusSearch])

  const showServerMessage = (
    success: boolean,
    message?: string,
    fallbackSuccess?: string,
    fallbackError?: string,
  ) => {
    if (success) {
      showToast({
        id: `circulation-return-${Date.now()}`,
        title: message ?? fallbackSuccess ?? '',
        color: 'success',
        iconType: 'check',
      })

      return
    }

    showToast({
      id: `circulation-return-${Date.now()}`,
      title: message ?? fallbackError ?? '',
      color: 'danger',
      iconType: 'alert',
    })
  }

  const onSearch = async (searchQuery = query) => {
    const trimmed = searchQuery.trim()
    if (!trimmed) {
      return
    }

    const response = await searchMutation.mutateAsync({ query: trimmed })
    if (!response.success) {
      setResults([])
      showServerMessage(
        false,
        response.message,
        undefined,
        formatMessage({
          defaultMessage: 'Nenhum exemplar encontrado',
          id: 'circulation.return.search.empty',
        }),
      )
      focusSearch()
      return
    }

    const data = response.search.data
    setResults(data)

    const uniqueBag = findUniqueHoldingIdentifierMatch(trimmed, data)
    if (uniqueBag?.lending && uniqueBag.lending.returnDate === undefined) {
      await onReturn(uniqueBag.lending.id)
    } else {
      focusSearch()
    }
  }

  const onReturn = async (lendingId: number) => {
    const response = await returnMutation.mutateAsync(lendingId)
    if (!response.success) {
      showServerMessage(
        false,
        response.message,
        undefined,
        formatMessage({
          defaultMessage: 'Falha na devolução',
          id: 'circulation.return.error.return_failed',
        }),
      )
      focusSearch()
      return
    }

    showServerMessage(
      true,
      response.message,
      formatMessage({
        defaultMessage: 'Devolução realizada',
        id: 'circulation.return.success.returned',
      }),
    )

    setLastReturn({
      holdingLendingBag: response.data,
      undoAvailableUntil: response.undo_available_until,
      nextReservation: response.next_reservation,
    })

    setSlip((prev) => [
      {
        lendingId,
        bag: response.data,
        returnedAt: Date.now(),
      },
      ...prev.filter((item) => item.lendingId !== lendingId),
    ])

    setQuery('')
    setResults(null)
    focusSearch()
  }

  const onUndo = async () => {
    if (!lastReturn?.holdingLendingBag.lending) {
      return
    }

    const lendingId = lastReturn.holdingLendingBag.lending.id
    const response = await undoMutation.mutateAsync(lendingId)
    if (!response.success) {
      showServerMessage(
        false,
        response.message,
        undefined,
        formatMessage({
          defaultMessage: 'Não foi possível desfazer',
          id: 'circulation.return.error.undo_failed',
        }),
      )
      return
    }

    showServerMessage(
      true,
      response.message,
      formatMessage({
        defaultMessage: 'Devolução desfeita',
        id: 'circulation.return.success.undone',
      }),
    )
    setSlip((prev) => prev.filter((item) => item.lendingId !== lendingId))
    setLastReturn(null)
    focusSearch()
  }

  const onPayFine = async (fineId: number, exempt = false) => {
    const response = await payFineMutation.mutateAsync({ fineId, exempt })
    if (!response.success) {
      showServerMessage(
        false,
        response.message,
        undefined,
        formatMessage({
          defaultMessage: 'Falha ao atualizar multa',
          id: 'circulation.return.error.fine_update_failed',
        }),
      )
      return
    }
    showServerMessage(
      true,
      response.message,
      exempt
        ? formatMessage({
            defaultMessage: 'Multa dispensada',
            id: 'circulation.return.success.fine_waived',
          })
        : formatMessage({
            defaultMessage: 'Multa paga',
            id: 'circulation.return.success.fine_paid',
          }),
    )
    setLastReturn((prev) => {
      if (!prev?.holdingLendingBag.lendingFine) {
        return prev
      }
      return {
        ...prev,
        holdingLendingBag: {
          ...prev.holdingLendingBag,
          lendingFine: {
            ...prev.holdingLendingBag.lendingFine,
            payment:
              new Date().toISOString() as typeof prev.holdingLendingBag.lendingFine.payment,
            value: exempt ? 0 : prev.holdingLendingBag.lendingFine.value,
          },
        },
      }
    })
  }

  const onAdjustFine = async () => {
    if (adjustFineId === null) {
      return
    }
    const value = Number(adjustValue.replace(',', '.'))
    if (Number.isNaN(value) || value < 0) {
      showToast({
        id: `circulation-return-invalid-fine-${Date.now()}`,
        title: formatMessage({
          defaultMessage: 'Valor inválido',
          id: 'circulation.return.error.invalid_fine_value',
        }),
        color: 'danger',
        iconType: 'alert',
      })
      return
    }

    const response = await adjustFineMutation.mutateAsync({
      fineId: adjustFineId,
      value,
    })
    if (!response.success) {
      showServerMessage(
        false,
        response.message,
        undefined,
        formatMessage({
          defaultMessage: 'Falha ao ajustar multa',
          id: 'circulation.return.error.fine_adjust_failed',
        }),
      )
      return
    }

    showServerMessage(
      true,
      response.message,
      formatMessage({
        defaultMessage: 'Multa ajustada',
        id: 'circulation.return.success.fine_adjusted',
      }),
    )
    setLastReturn((prev) =>
      prev?.holdingLendingBag.lendingFine
        ? {
            ...prev,
            holdingLendingBag: {
              ...prev.holdingLendingBag,
              lendingFine: {
                ...prev.holdingLendingBag.lendingFine,
                value,
              },
            },
          }
        : prev,
    )
    setAdjustFineId(null)
    setAdjustValue('')
  }

  const onPrintSlip = async () => {
    if (slip.length === 0) {
      return
    }
    const response = await printReceiptMutation.mutateAsync(
      slip.map((item) => item.lendingId),
    )
    if (!response.success) {
      showServerMessage(
        false,
        response.message,
        undefined,
        formatMessage({
          defaultMessage: 'Falha ao gerar recibo',
          id: 'circulation.return.error.receipt_failed',
        }),
      )
      return
    }

    const printWindow = window.open('', '_blank')
    if (printWindow) {
      printWindow.document.write(response.receipt)
      printWindow.document.close()
      printWindow.focus()
      printWindow.print()
    }
  }

  const onClearSlip = () => {
    setSlip([])
  }

  const isBusy =
    searchMutation.isPending ||
    returnMutation.isPending ||
    undoMutation.isPending ||
    payFineMutation.isPending ||
    adjustFineMutation.isPending ||
    printReceiptMutation.isPending

  const undoStillAvailable =
    lastReturn?.undoAvailableUntil !== undefined &&
    Date.now() < lastReturn.undoAvailableUntil

  return {
    query,
    setQuery,
    results,
    slip,
    lastReturn,
    searchInputRef,
    isBusy,
    undoStillAvailable,
    adjustFineId,
    setAdjustFineId,
    adjustValue,
    setAdjustValue,
    onSearch,
    onReturn,
    onUndo,
    onPayFine,
    onAdjustFine,
    onPrintSlip,
    onClearSlip,
  }
}

export default useCirculationReturn
