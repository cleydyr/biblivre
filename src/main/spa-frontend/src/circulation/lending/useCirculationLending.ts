import { useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { useIntl } from 'react-intl'

import {
  circulationQueryKeys,
  useCirculationUserTabData,
} from '../../api-helpers/circulation/hooks'
import {
  useLendHoldingMutation,
  useLendingHoldingSearchMutation,
  useLendingReaderSearchMutation,
  usePrintLendingReceiptMutation,
  useRenewLendingMutation,
  useReturnLendingImmediateMutation,
} from '../../api-helpers/lending/hooks'
import { useUserType } from '../../api-helpers/user-type/hooks'
import { useToasts } from '../../toasts/useToasts'
import { findUniqueHoldingIdentifierMatch } from '../return/useCirculationReturn'

import {
  applyBagToReader,
  applyBagToResults,
  getHoldingStatus,
  getLendBlockReason,
  getReceiptLendingIds,
  isReservedForOtherReaders,
  sumPendingFines,
} from './lendingState'

import type { HoldingLendingBag } from '../../api-helpers/circulation/response-types'
import type { LendingReader } from '../../api-helpers/lending/response-types'

import type { SessionOperation } from './lendingState'

export type Paging = {
  /** 1-based */
  page: number
  pageCount: number
  total: number
  pageSize: number
}

type PaginatedSearch = {
  success: boolean
  search?: {
    page: number
    page_count: number
    record_count: number
    records_per_page: number
  }
}

const toPaging = ({ search }: PaginatedSearch): Paging | null =>
  search
    ? {
        page: search.page,
        pageCount: search.page_count,
        total: search.record_count,
        pageSize: search.records_per_page,
      }
    : null

const useCirculationLending = () => {
  const { formatMessage } = useIntl()
  const { showToast } = useToasts()
  const queryClient = useQueryClient()

  const [readerQuery, setReaderQuery] = useState('')
  const [readerResults, setReaderResults] = useState<LendingReader[] | null>(
    null,
  )
  const [readerPaging, setReaderPaging] = useState<Paging | null>(null)
  const [readerEmptyMessage, setReaderEmptyMessage] = useState<string>()
  const [reader, setReader] = useState<LendingReader | null>(null)

  const [holdingQuery, setHoldingQuery] = useState('')
  const [onlyLent, setOnlyLent] = useState(false)
  const [holdingResults, setHoldingResults] = useState<
    HoldingLendingBag[] | null
  >(null)
  const [holdingPaging, setHoldingPaging] = useState<Paging | null>(null)
  const [holdingEmptyMessage, setHoldingEmptyMessage] = useState<string>()

  const [operations, setOperations] = useState<SessionOperation[]>([])

  const readerSearchMutation = useLendingReaderSearchMutation()
  const holdingSearchMutation = useLendingHoldingSearchMutation()
  const lendMutation = useLendHoldingMutation()
  const renewMutation = useRenewLendingMutation()
  const returnMutation = useReturnLendingImmediateMutation()
  const printReceiptMutation = usePrintLendingReceiptMutation()

  const readerId = reader?.user.id
  const { data: userType } = useUserType(reader?.user.type)
  const { data: finesData, isFetching: isFetchingFines } =
    useCirculationUserTabData(readerId ?? 0, 'fines', readerId !== undefined)

  const lendingLimit = userType?.lendingLimit
  const blockReason = reader ? getLendBlockReason(reader, lendingLimit) : null
  const pendingFines =
    finesData && !isFetchingFines
      ? sumPendingFines(finesData.data.data ?? [])
      : undefined

  const notify = (
    success: boolean,
    message: string | undefined,
    fallback: string,
  ) => {
    showToast({
      id: `circulation-lending-${Date.now()}`,
      title: message || fallback,
      color: success ? 'success' : 'danger',
      iconType: success ? 'check' : 'error',
    })
  }

  const recordOperation = (
    kind: SessionOperation['kind'],
    bag: HoldingLendingBag,
  ) => {
    if (!bag.lending) {
      return
    }

    const { id: lendingId, userId } = bag.lending

    setOperations((previous) => [
      ...previous,
      {
        kind,
        readerId: userId,
        lendingId,
        holdingId: bag.holding.id,
        at: Date.now(),
      },
    ])
  }

  const applyBag = (bag: HoldingLendingBag) => {
    setReader((current) => current && applyBagToReader(current, bag))
    setHoldingResults((current) => applyBagToResults(current, bag))
  }

  const searchReaders = async (page = 1) => {
    const response = await readerSearchMutation.mutateAsync({
      query: readerQuery.trim(),
      page,
    })

    setReaderPaging(toPaging(response))

    if (!response.success) {
      setReaderResults([])
      setReaderEmptyMessage(response.message)
      return
    }

    setReaderResults(response.search.data)
    setReaderEmptyMessage(undefined)
  }

  const selectReader = (selected: LendingReader) => {
    setReader(selected)
    setHoldingQuery('')
    setHoldingResults(null)
    setHoldingPaging(null)
  }

  const clearReader = () => {
    setReader(null)
    setHoldingResults(null)
    setHoldingPaging(null)
  }

  const lend = async (bag: HoldingLendingBag) => {
    if (!reader) {
      return
    }

    const response = await lendMutation.mutateAsync({
      holdingId: bag.holding.id,
      userId: reader.user.id,
    })

    notify(
      response.success,
      response.message,
      response.success
        ? formatMessage({
            defaultMessage: 'Empréstimo realizado',
            id: 'circulation.lending.page.toast.lent',
          })
        : formatMessage({
            defaultMessage: 'Não foi possível realizar o empréstimo',
            id: 'circulation.lending.page.toast.lend_failed',
          }),
    )

    if (response.success) {
      applyBag(response.data)
      recordOperation('lend', response.data)
    }
  }

  const searchHoldings = async (page = 1, includeLentOnly = onlyLent) => {
    const query = holdingQuery.trim()
    const response = await holdingSearchMutation.mutateAsync({
      query,
      holding_list_lendings: includeLentOnly,
      page,
    })

    setHoldingPaging(toPaging(response))

    if (!response.success) {
      setHoldingResults([])
      setHoldingEmptyMessage(response.message)
      return
    }

    const results = response.search.data
    setHoldingResults(results)
    setHoldingEmptyMessage(undefined)

    // A scanned accession number that matches one available holding is lent
    // right away, like the Return screen does for returns. A title reserved
    // for other readers is left on screen so the reservation callout is seen.
    const match =
      page === 1 ? findUniqueHoldingIdentifierMatch(query, results) : null

    if (
      match &&
      reader &&
      blockReason === null &&
      getHoldingStatus(match, reader.user.id) === 'available' &&
      !isReservedForOtherReaders(match, reader)
    ) {
      await lend(match)
      setHoldingQuery('')
    }
  }

  const changeOnlyLent = (value: boolean) => {
    setOnlyLent(value)

    if (holdingResults !== null) {
      void searchHoldings(1, value)
    }
  }

  const renew = async (bag: HoldingLendingBag) => {
    if (!bag.lending) {
      return
    }

    const response = await renewMutation.mutateAsync(bag.lending.id)

    notify(
      response.success,
      response.message,
      response.success
        ? formatMessage({
            defaultMessage: 'Empréstimo renovado',
            id: 'circulation.lending.page.toast.renewed',
          })
        : formatMessage({
            defaultMessage: 'Não foi possível renovar o empréstimo',
            id: 'circulation.lending.page.toast.renew_failed',
          }),
    )

    if (response.success) {
      applyBag(response.data)
      recordOperation('renew', response.data)
    }
  }

  const giveBack = async (bag: HoldingLendingBag) => {
    if (!bag.lending) {
      return
    }

    const { userId } = bag.lending
    const response = await returnMutation.mutateAsync(bag.lending.id)

    notify(
      response.success,
      response.message,
      response.success
        ? formatMessage({
            defaultMessage: 'Devolução realizada',
            id: 'circulation.lending.page.toast.returned',
          })
        : formatMessage({
            defaultMessage: 'Não foi possível realizar a devolução',
            id: 'circulation.lending.page.toast.return_failed',
          }),
    )

    if (response.success) {
      applyBag(response.data)
      recordOperation('return', response.data)
      // A late return creates a fine on the server.
      void queryClient.invalidateQueries({
        queryKey: circulationQueryKeys.userTabData(userId, 'fines'),
      })
    }
  }

  const receiptLendingIds = readerId
    ? getReceiptLendingIds(operations, readerId)
    : []

  const printReceipt = async () => {
    if (receiptLendingIds.length === 0) {
      return
    }

    const response = await printReceiptMutation.mutateAsync(receiptLendingIds)

    if (!response.success) {
      notify(
        false,
        response.message,
        formatMessage({
          defaultMessage: 'Não foi possível gerar o recibo',
          id: 'circulation.lending.page.toast.receipt_failed',
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

  const isMutating =
    lendMutation.isPending ||
    renewMutation.isPending ||
    returnMutation.isPending ||
    printReceiptMutation.isPending

  return {
    readerSearch: {
      query: readerQuery,
      setQuery: setReaderQuery,
      results: readerResults,
      paging: readerPaging,
      emptyMessage: readerEmptyMessage,
      isLoading: readerSearchMutation.isPending,
      onSearch: searchReaders,
    },
    holdingSearch: {
      query: holdingQuery,
      setQuery: setHoldingQuery,
      onlyLent,
      setOnlyLent: changeOnlyLent,
      results: holdingResults,
      paging: holdingPaging,
      emptyMessage: holdingEmptyMessage,
      isLoading: holdingSearchMutation.isPending,
      onSearch: searchHoldings,
    },
    reader,
    userType,
    pendingFines,
    blockReason,
    receiptCount: receiptLendingIds.length,
    isMutating,
    onSelectReader: selectReader,
    onClearReader: clearReader,
    onLend: lend,
    onRenew: renew,
    onReturn: giveBack,
    onPrintReceipt: printReceipt,
  }
}

export type CirculationLendingState = ReturnType<typeof useCirculationLending>

export default useCirculationLending
