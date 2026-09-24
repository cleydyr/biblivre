import { useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { useIntl } from 'react-intl'
import { useNavigate } from 'react-router-dom'

import { circulationQueryKeys } from '../../api-helpers/circulation/hooks'
import {
  useCreateLendingMutation,
  useLendingHoldingSearchMutation,
  useLendingUserSearchMutation,
  usePrintLendingReceiptMutation,
  useRenewLendingMutation,
} from '../../api-helpers/lending/hooks'
import { useToasts } from '../../toasts/useToasts'

import {
  getLendingEligibility,
  getLendingErrorMessage,
  getRenewalEligibility,
} from './logic'

import type {
  HoldingLendingBag,
  LendingBag,
} from '../../api-helpers/circulation/response-types'
import type { LendingPatron } from '../../api-helpers/lending/response-types'

const useCirculationLending = () => {
  const { formatMessage } = useIntl()
  const { showToast } = useToasts()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const [patronQuery, setPatronQuery] = useState('')
  const [patrons, setPatrons] = useState<LendingPatron[] | null>(null)
  const [selectedPatron, setSelectedPatron] = useState<LendingPatron | null>(
    null,
  )
  const [holdingQuery, setHoldingQuery] = useState('')
  const [holdings, setHoldings] = useState<HoldingLendingBag[] | null>(null)
  const [receiptLendingIds, setReceiptLendingIds] = useState<number[]>([])

  const userSearchMutation = useLendingUserSearchMutation()
  const holdingSearchMutation = useLendingHoldingSearchMutation()
  const createMutation = useCreateLendingMutation()
  const renewMutation = useRenewLendingMutation()
  const printReceiptMutation = usePrintLendingReceiptMutation()

  const showResult = (success: boolean, message: string, id: string) => {
    showToast({
      id: `${id}-${Date.now()}`,
      title: message,
      color: success ? 'success' : 'danger',
      iconType: success ? 'check' : 'alert',
    })
  }

  const onSearchPatrons = async () => {
    try {
      const response = await userSearchMutation.mutateAsync({
        query: patronQuery.trim(),
      })
      if (!response.success) {
        setPatrons([])
        showResult(false, response.message, 'circulation-lending-user-search')
        return
      }

      setPatrons(response.search.data)
    } catch (error) {
      setPatrons([])
      showResult(
        false,
        getLendingErrorMessage(
          error,
          undefined,
          formatMessage({
            defaultMessage: 'Falha ao pesquisar usuários',
            id: 'circulation.lending.error.user_search',
          }),
        ),
        'circulation-lending-user-search',
      )
    }
  }

  const onSelectPatron = (patron: LendingPatron) => {
    setSelectedPatron(patron)
    setHoldings(null)
    setHoldingQuery('')
    setReceiptLendingIds([])
  }

  const onClearPatron = () => {
    setSelectedPatron(null)
    setHoldings(null)
    setHoldingQuery('')
    setReceiptLendingIds([])
  }

  const onSearchHoldings = async () => {
    if (!selectedPatron || !holdingQuery.trim()) {
      return
    }

    try {
      const response = await holdingSearchMutation.mutateAsync({
        query: holdingQuery.trim(),
        holding_list_lendings: true,
      })
      if (!response.success) {
        setHoldings([])
        showResult(
          false,
          response.message,
          'circulation-lending-holding-search',
        )
        return
      }

      setHoldings(response.search.data)
    } catch (error) {
      setHoldings([])
      showResult(
        false,
        getLendingErrorMessage(
          error,
          undefined,
          formatMessage({
            defaultMessage: 'Falha ao pesquisar exemplares',
            id: 'circulation.lending.error.holding_search',
          }),
        ),
        'circulation-lending-holding-search',
      )
    }
  }

  const updateAfterMutation = (
    oldLendingId: number | null,
    lendingBag: LendingBag,
  ) => {
    setSelectedPatron((patron) => {
      if (!patron) {
        return patron
      }

      const lendings =
        oldLendingId === null
          ? [...patron.lendingInfo, lendingBag]
          : patron.lendingInfo.map((item) =>
              item.lending.id === oldLendingId ? lendingBag : item,
            )

      return { ...patron, lendingInfo: lendings }
    })

    setHoldings(
      (current) =>
        current?.map((item) => {
          const matchesHolding = item.holding.id === lendingBag.holding.id
          const matchesLending =
            oldLendingId !== null && item.lending?.id === oldLendingId
          return matchesHolding || matchesLending ? lendingBag : item
        }) ?? current,
    )

    setReceiptLendingIds((current) => [
      ...current.filter((id) => id !== lendingBag.lending.id),
      lendingBag.lending.id,
    ])

    void queryClient.invalidateQueries({
      queryKey: circulationQueryKeys.userTabData(
        lendingBag.user.id,
        'lendings',
      ),
    })
  }

  const onLend = async (holdingBag: HoldingLendingBag) => {
    if (
      !selectedPatron ||
      getLendingEligibility(holdingBag, selectedPatron) !== 'eligible'
    ) {
      return
    }

    try {
      const response = await createMutation.mutateAsync({
        holdingId: holdingBag.holding.id,
        userId: selectedPatron.user.id,
      })
      if (!response.success) {
        showResult(false, response.message, 'circulation-lending-create')
        return
      }

      updateAfterMutation(null, response.data)
      showResult(
        true,
        response.message ??
          formatMessage({
            defaultMessage: 'Empréstimo realizado',
            id: 'circulation.lending.success.lent',
          }),
        'circulation-lending-create',
      )
    } catch (error) {
      showResult(
        false,
        getLendingErrorMessage(
          error,
          undefined,
          formatMessage({
            defaultMessage: 'Não foi possível realizar o empréstimo',
            id: 'circulation.lending.error.lend_failed',
          }),
        ),
        'circulation-lending-create',
      )
    }
  }

  const onRenew = async (lendingId: number) => {
    if (!selectedPatron) {
      return
    }

    const lendingBag = selectedPatron.lendingInfo.find(
      (item) => item.lending.id === lendingId,
    )
    if (
      !lendingBag ||
      getRenewalEligibility(lendingBag.lending, selectedPatron) !== 'eligible'
    ) {
      return
    }

    try {
      const response = await renewMutation.mutateAsync(lendingId)
      if (!response.success) {
        showResult(false, response.message, 'circulation-lending-renew')
        return
      }

      updateAfterMutation(lendingId, response.data)
      showResult(
        true,
        response.message ??
          formatMessage({
            defaultMessage: 'Empréstimo renovado',
            id: 'circulation.lending.success.renewed',
          }),
        'circulation-lending-renew',
      )
    } catch (error) {
      showResult(
        false,
        getLendingErrorMessage(
          error,
          undefined,
          formatMessage({
            defaultMessage: 'Não foi possível renovar o empréstimo',
            id: 'circulation.lending.error.renew_failed',
          }),
        ),
        'circulation-lending-renew',
      )
    }
  }

  const onPrintReceipt = async () => {
    if (receiptLendingIds.length === 0) {
      return
    }

    try {
      const response = await printReceiptMutation.mutateAsync(receiptLendingIds)
      if (!response.success) {
        showResult(false, response.message, 'circulation-lending-receipt')
        return
      }

      const printWindow = window.open('', '_blank')
      if (printWindow) {
        printWindow.document.write(response.receipt)
        printWindow.document.close()
        printWindow.focus()
        printWindow.print()
      }
    } catch (error) {
      showResult(
        false,
        getLendingErrorMessage(
          error,
          undefined,
          formatMessage({
            defaultMessage: 'Falha ao gerar recibo',
            id: 'circulation.lending.error.receipt_failed',
          }),
        ),
        'circulation-lending-receipt',
      )
    }
  }

  const isBusy =
    userSearchMutation.isPending ||
    holdingSearchMutation.isPending ||
    createMutation.isPending ||
    renewMutation.isPending ||
    printReceiptMutation.isPending

  return {
    patronQuery,
    setPatronQuery,
    patrons,
    selectedPatron,
    holdingQuery,
    setHoldingQuery,
    holdings,
    receiptLendingIds,
    isBusy,
    onSearchPatrons,
    onSelectPatron,
    onClearPatron,
    onSearchHoldings,
    onLend,
    onRenew,
    onPrintReceipt,
    onOpenReturns: () => navigate('/spa/circulation_return'),
  }
}

export default useCirculationLending
