import {
  EuiFieldSearch,
  EuiFlexGroup,
  EuiFormRow,
  EuiPanel,
} from '@elastic/eui'
import { FormattedMessage, useIntl } from 'react-intl'

import LastReturnPanel from './LastReturnPanel'
import ReturnSlipPanel from './ReturnSlipPanel'
import SearchResults from './SearchResults'

import type { FC, FormEvent, KeyboardEvent, MutableRefObject } from 'react'

import type { HoldingLendingBag } from '../../api-helpers/circulation/response-types'

import type { LastReturnState, ReturnSlipItem } from './useCirculationReturn'

type Props = {
  query: string
  setQuery: (value: string) => void
  results: HoldingLendingBag[] | null
  slip: ReturnSlipItem[]
  lastReturn: LastReturnState | null
  searchInputRef: MutableRefObject<HTMLInputElement | null>
  isBusy: boolean
  undoStillAvailable: boolean
  adjustFineId: number | null
  setAdjustFineId: (id: number | null) => void
  adjustValue: string
  setAdjustValue: (value: string) => void
  onSearch: (query?: string) => void
  onReturn: (lendingId: number) => void
  onUndo: () => void
  onPayFine: (fineId: number, exempt?: boolean) => void
  onAdjustFine: () => void
  onPrintSlip: () => void
  onClearSlip: () => void
}

const CirculationReturnView: FC<Props> = ({
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
}) => {
  const { formatMessage } = useIntl()

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSearch()
  }

  return (
    <EuiFlexGroup direction='column' gutterSize='l'>
      <EuiPanel paddingSize='m'>
        <form onSubmit={handleSubmit}>
          <EuiFormRow
            fullWidth
            label={
              <FormattedMessage
                defaultMessage='Tombo, título ou autor'
                id='circulation.return.search.label'
              />
            }
          >
            <EuiFieldSearch
              autoFocus
              fullWidth
              compressed={false}
              disabled={isBusy}
              inputRef={(node) => {
                searchInputRef.current = node
              }}
              isLoading={isBusy}
              placeholder={formatMessage({
                defaultMessage: 'Escaneie o tombo ou busque por título/autor',
                id: 'circulation.return.search.placeholder',
              })}
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              onKeyDown={(event: KeyboardEvent<HTMLInputElement>) => {
                if (event.key === 'Enter') {
                  event.preventDefault()
                  onSearch()
                }
              }}
            />
          </EuiFormRow>
        </form>
      </EuiPanel>

      {lastReturn && (
        <LastReturnPanel
          adjustFineId={adjustFineId}
          adjustValue={adjustValue}
          isBusy={isBusy}
          lastReturn={lastReturn}
          setAdjustFineId={setAdjustFineId}
          setAdjustValue={setAdjustValue}
          undoStillAvailable={undoStillAvailable}
          onAdjustFine={onAdjustFine}
          onPayFine={onPayFine}
          onUndo={onUndo}
        />
      )}

      {results !== null && (
        <SearchResults isBusy={isBusy} results={results} onReturn={onReturn} />
      )}

      <ReturnSlipPanel
        isBusy={isBusy}
        slip={slip}
        onClearSlip={onClearSlip}
        onPrintSlip={onPrintSlip}
      />
    </EuiFlexGroup>
  )
}

export default CirculationReturnView
