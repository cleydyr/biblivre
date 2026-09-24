import {
  EuiAccordion,
  EuiBadge,
  EuiButton,
  EuiButtonEmpty,
  EuiCallOut,
  EuiEmptyPrompt,
  EuiFieldSearch,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFormRow,
  EuiHorizontalRule,
  EuiPanel,
  EuiSpacer,
  EuiText,
  EuiTitle,
} from '@elastic/eui'
import { Fragment } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'

import CirculationUserFinesTab from '../CirculationUserFinesTab'
import CirculationUserSummaryPanel from '../CirculationUserSummaryPanel'
import HoldingLendingSummary from '../return/HoldingLendingSummary'

import {
  getLendingEligibility,
  getRenewalEligibility,
  hasReservationPriorityWarning,
} from './logic'

import type { FC, FormEvent } from 'react'

import type {
  HoldingLendingBag,
  LendingBag,
} from '../../api-helpers/circulation/response-types'
import type { LendingPatron } from '../../api-helpers/lending/response-types'

type Props = {
  patronQuery: string
  setPatronQuery: (value: string) => void
  patrons: LendingPatron[] | null
  selectedPatron: LendingPatron | null
  holdingQuery: string
  setHoldingQuery: (value: string) => void
  holdings: HoldingLendingBag[] | null
  receiptLendingIds: number[]
  isBusy: boolean
  onSearchPatrons: () => void
  onSelectPatron: (patron: LendingPatron) => void
  onClearPatron: () => void
  onSearchHoldings: () => void
  onLend: (holdingBag: HoldingLendingBag) => void
  onRenew: (lendingId: number) => void
  onPrintReceipt: () => void
  onOpenReturns: () => void
}

const CirculationLendingView: FC<Props> = ({
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
  onOpenReturns,
}) => {
  if (!selectedPatron) {
    return (
      <PatronSearch
        isBusy={isBusy}
        patrons={patrons}
        query={patronQuery}
        setQuery={setPatronQuery}
        onSearch={onSearchPatrons}
        onSelect={onSelectPatron}
      />
    )
  }

  return (
    <EuiFlexGroup direction='column' gutterSize='l'>
      <SelectedPatron
        isBusy={isBusy}
        patron={selectedPatron}
        receiptCount={receiptLendingIds.length}
        onClear={onClearPatron}
        onOpenReturns={onOpenReturns}
        onPrintReceipt={onPrintReceipt}
        onRenew={onRenew}
      />
      <HoldingSearch
        holdings={holdings}
        isBusy={isBusy}
        patron={selectedPatron}
        query={holdingQuery}
        setQuery={setHoldingQuery}
        onLend={onLend}
        onRenew={onRenew}
        onSearch={onSearchHoldings}
      />
    </EuiFlexGroup>
  )
}

const PatronSearch: FC<{
  query: string
  setQuery: (value: string) => void
  patrons: LendingPatron[] | null
  isBusy: boolean
  onSearch: () => void
  onSelect: (patron: LendingPatron) => void
}> = ({ query, setQuery, patrons, isBusy, onSearch, onSelect }) => {
  const { formatMessage } = useIntl()

  const onSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSearch()
  }

  return (
    <EuiFlexGroup direction='column' gutterSize='l'>
      <EuiPanel hasBorder paddingSize='l'>
        <form onSubmit={onSubmit}>
          <EuiFormRow
            fullWidth
            label={
              <FormattedMessage
                defaultMessage='Nome ou matrícula do usuário'
                id='circulation.lending.patron_search.label'
              />
            }
          >
            <EuiFieldSearch
              autoFocus
              fullWidth
              isLoading={isBusy}
              placeholder={formatMessage({
                defaultMessage: 'Pesquise e selecione quem receberá o exemplar',
                id: 'circulation.lending.patron_search.placeholder',
              })}
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              onSearch={onSearch}
            />
          </EuiFormRow>
        </form>
      </EuiPanel>

      <PatronResults isBusy={isBusy} patrons={patrons} onSelect={onSelect} />
    </EuiFlexGroup>
  )
}

const PatronResults: FC<{
  patrons: LendingPatron[] | null
  isBusy: boolean
  onSelect: (patron: LendingPatron) => void
}> = ({ patrons, isBusy, onSelect }) => {
  if (patrons === null) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Pesquise um usuário para iniciar o atendimento.'
            id='circulation.lending.patron_search.initial.body'
          />
        }
        iconType='user'
      />
    )
  }

  if (patrons.length === 0) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Tente outro nome, matrícula ou campo de cadastro.'
            id='circulation.lending.patron_search.empty.body'
          />
        }
        iconType='users'
        title={
          <FormattedMessage
            defaultMessage='Nenhum usuário encontrado'
            id='circulation.lending.patron_search.empty.title'
          />
        }
      />
    )
  }

  return patrons.map((patron) => (
    <EuiPanel key={patron.id} hasBorder paddingSize='m'>
      <EuiFlexGroup alignItems='center'>
        <EuiFlexItem>
          <CirculationUserSummaryPanel user={patron.user} />
          <EuiSpacer size='s' />
          <EuiText size='s'>
            <FormattedMessage
              defaultMessage='{count, plural, =0 {Nenhum empréstimo ativo} one {# empréstimo ativo} other {# empréstimos ativos}}'
              id='circulation.lending.patron_search.active_count'
              values={{ count: patron.lendingInfo.length }}
            />
          </EuiText>
        </EuiFlexItem>
        <EuiFlexItem grow={false}>
          <EuiButton disabled={isBusy} onClick={() => onSelect(patron)}>
            <FormattedMessage
              defaultMessage='Selecionar usuário'
              id='circulation.lending.button.select_patron'
            />
          </EuiButton>
        </EuiFlexItem>
      </EuiFlexGroup>
    </EuiPanel>
  ))
}

const SelectedPatron: FC<{
  patron: LendingPatron
  isBusy: boolean
  receiptCount: number
  onClear: () => void
  onRenew: (lendingId: number) => void
  onPrintReceipt: () => void
  onOpenReturns: () => void
}> = ({
  patron,
  isBusy,
  receiptCount,
  onClear,
  onRenew,
  onPrintReceipt,
  onOpenReturns,
}) => {
  const activeLendings = patron.lendingInfo.filter(
    (item) => item.lending.returnDate === undefined,
  )

  return (
    <EuiPanel hasBorder paddingSize='l'>
      <EuiFlexGroup alignItems='flexStart'>
        <EuiFlexItem>
          <EuiTitle size='s'>
            <h2>
              <FormattedMessage
                defaultMessage='Usuário selecionado'
                id='circulation.lending.selected_patron'
              />
            </h2>
          </EuiTitle>
          <EuiSpacer size='m' />
          <CirculationUserSummaryPanel user={patron.user} />
        </EuiFlexItem>
        <EuiFlexItem grow={false}>
          <EuiFlexGroup direction='column' gutterSize='s'>
            <EuiButton
              disabled={receiptCount === 0 || isBusy}
              iconType='print'
              onClick={onPrintReceipt}
            >
              <FormattedMessage
                defaultMessage='Imprimir recibo ({count})'
                id='circulation.lending.button.print_receipt'
                values={{ count: receiptCount }}
              />
            </EuiButton>
            <EuiButtonEmpty disabled={isBusy} onClick={onClear}>
              <FormattedMessage
                defaultMessage='Trocar usuário'
                id='circulation.lending.button.change_patron'
              />
            </EuiButtonEmpty>
          </EuiFlexGroup>
        </EuiFlexItem>
      </EuiFlexGroup>

      <EuiHorizontalRule margin='m' />

      <EuiTitle size='xs'>
        <h3>
          <FormattedMessage
            defaultMessage='Empréstimos ativos ({count})'
            id='circulation.lending.active_loans'
            values={{ count: activeLendings.length }}
          />
        </h3>
      </EuiTitle>
      <EuiSpacer size='s' />
      {activeLendings.length > 0 ? (
        <EuiFlexGroup direction='column' gutterSize='m'>
          {activeLendings.map((lendingBag) => (
            <ActiveLendingCard
              key={lendingBag.lending.id}
              isBusy={isBusy}
              lendingBag={lendingBag}
              patron={patron}
              onOpenReturns={onOpenReturns}
              onRenew={onRenew}
            />
          ))}
        </EuiFlexGroup>
      ) : (
        <EuiText color='subdued' size='s'>
          <FormattedMessage
            defaultMessage='Este usuário não possui empréstimos ativos.'
            id='circulation.lending.active_loans.empty'
          />
        </EuiText>
      )}

      <EuiSpacer size='m' />
      <EuiAccordion
        buttonContent={
          <FormattedMessage
            defaultMessage='Multas e pendências'
            id='circulation.lending.fines'
          />
        }
        id={`lending-fines-${patron.user.id}`}
        paddingSize='m'
      >
        <CirculationUserFinesTab userId={patron.user.id} />
      </EuiAccordion>
    </EuiPanel>
  )
}

const ActiveLendingCard: FC<{
  lendingBag: LendingBag
  patron: LendingPatron
  isBusy: boolean
  onRenew: (lendingId: number) => void
  onOpenReturns: () => void
}> = ({ lendingBag, patron, isBusy, onRenew, onOpenReturns }) => {
  const renewalEligibility = getRenewalEligibility(lendingBag.lending, patron)
  const isOverdue = renewalEligibility === 'overdue'

  return (
    <EuiPanel color='subdued' paddingSize='m'>
      <EuiFlexGroup alignItems='flexEnd'>
        <EuiFlexItem>
          <HoldingLendingSummary holdingLendingBag={lendingBag} />
          <EuiSpacer size='s' />
          {isOverdue ? (
            <EuiBadge color='danger'>
              <FormattedMessage
                defaultMessage='{days} dias em atraso'
                id='circulation.lending.overdue'
                values={{ days: lendingBag.lending.daysLate ?? 0 }}
              />
            </EuiBadge>
          ) : (
            <EuiBadge color='success'>
              <FormattedMessage
                defaultMessage='Em dia'
                id='circulation.lending.on_time'
              />
            </EuiBadge>
          )}
        </EuiFlexItem>
        <EuiFlexItem grow={false}>
          <EuiFlexGroup gutterSize='s' responsive={false}>
            <EuiButton
              disabled={renewalEligibility !== 'eligible' || isBusy}
              onClick={() => onRenew(lendingBag.lending.id)}
            >
              <FormattedMessage
                defaultMessage='Renovar'
                id='circulation.lending.button.renew'
              />
            </EuiButton>
            <EuiButtonEmpty disabled={isBusy} onClick={onOpenReturns}>
              <FormattedMessage
                defaultMessage='Devolver ou tratar multa'
                id='circulation.lending.button.open_return'
              />
            </EuiButtonEmpty>
          </EuiFlexGroup>
        </EuiFlexItem>
      </EuiFlexGroup>
    </EuiPanel>
  )
}

const HoldingSearch: FC<{
  query: string
  setQuery: (value: string) => void
  holdings: HoldingLendingBag[] | null
  patron: LendingPatron
  isBusy: boolean
  onSearch: () => void
  onLend: (holdingBag: HoldingLendingBag) => void
  onRenew: (lendingId: number) => void
}> = ({
  query,
  setQuery,
  holdings,
  patron,
  isBusy,
  onSearch,
  onLend,
  onRenew,
}) => {
  const { formatMessage } = useIntl()

  const onSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSearch()
  }

  return (
    <EuiFlexGroup direction='column' gutterSize='m'>
      <EuiPanel hasBorder paddingSize='l'>
        <form onSubmit={onSubmit}>
          <EuiFormRow
            fullWidth
            label={
              <FormattedMessage
                defaultMessage='Tombo patrimonial, título ou autor'
                id='circulation.lending.holding_search.label'
              />
            }
          >
            <EuiFieldSearch
              fullWidth
              isLoading={isBusy}
              placeholder={formatMessage({
                defaultMessage: 'Escaneie o tombo ou pesquise o exemplar',
                id: 'circulation.lending.holding_search.placeholder',
              })}
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              onSearch={onSearch}
            />
          </EuiFormRow>
        </form>
      </EuiPanel>

      <HoldingResults
        holdings={holdings}
        isBusy={isBusy}
        patron={patron}
        onLend={onLend}
        onRenew={onRenew}
      />
    </EuiFlexGroup>
  )
}

const HoldingResults: FC<{
  holdings: HoldingLendingBag[] | null
  patron: LendingPatron
  isBusy: boolean
  onLend: (holdingBag: HoldingLendingBag) => void
  onRenew: (lendingId: number) => void
}> = ({ holdings, patron, isBusy, onLend, onRenew }) => {
  if (holdings === null) {
    return null
  }

  if (holdings.length === 0) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Confira o tombo ou tente buscar por outro título ou autor.'
            id='circulation.lending.holding_search.empty.body'
          />
        }
        iconType='magnify'
        title={
          <FormattedMessage
            defaultMessage='Nenhum exemplar encontrado'
            id='circulation.lending.holding_search.empty.title'
          />
        }
      />
    )
  }

  return holdings.map((holdingBag) => (
    <HoldingCard
      key={holdingBag.holding.id}
      holdingBag={holdingBag}
      isBusy={isBusy}
      patron={patron}
      onLend={onLend}
      onRenew={onRenew}
    />
  ))
}

const HoldingCard: FC<{
  holdingBag: HoldingLendingBag
  patron: LendingPatron
  isBusy: boolean
  onLend: (holdingBag: HoldingLendingBag) => void
  onRenew: (lendingId: number) => void
}> = ({ holdingBag, patron, isBusy, onLend, onRenew }) => {
  const lendingEligibility = getLendingEligibility(holdingBag, patron)
  const openLending =
    holdingBag.lending?.returnDate === undefined
      ? holdingBag.lending
      : undefined
  const renewalEligibility = openLending
    ? getRenewalEligibility(openLending, patron)
    : null
  const reservationWarning = hasReservationPriorityWarning(holdingBag, patron)

  return (
    <EuiPanel hasBorder paddingSize='m'>
      <EuiFlexGroup alignItems='flexEnd'>
        <EuiFlexItem>
          <HoldingLendingSummary holdingLendingBag={holdingBag} />
          {reservationWarning ? <FragmentReservationWarning /> : null}
          {openLending && renewalEligibility === 'different_patron' ? (
            <EuiCallOut
              announceOnMount
              color='warning'
              iconType='user'
              size='s'
              title={
                <FormattedMessage
                  defaultMessage='Exemplar emprestado a outro usuário'
                  id='circulation.lending.holding.other_patron'
                />
              }
            />
          ) : null}
        </EuiFlexItem>
        <EuiFlexItem grow={false}>
          <HoldingAction
            holdingBag={holdingBag}
            isBusy={isBusy}
            lendingEligibility={lendingEligibility}
            openLendingId={
              renewalEligibility === 'eligible' ? openLending?.id : undefined
            }
            onLend={onLend}
            onRenew={onRenew}
          />
        </EuiFlexItem>
      </EuiFlexGroup>
    </EuiPanel>
  )
}

const HoldingAction: FC<{
  holdingBag: HoldingLendingBag
  lendingEligibility: ReturnType<typeof getLendingEligibility>
  openLendingId: number | undefined
  isBusy: boolean
  onLend: (holdingBag: HoldingLendingBag) => void
  onRenew: (lendingId: number) => void
}> = ({
  holdingBag,
  lendingEligibility,
  openLendingId,
  isBusy,
  onLend,
  onRenew,
}) => {
  if (openLendingId !== undefined) {
    return (
      <EuiButton disabled={isBusy} onClick={() => onRenew(openLendingId)}>
        <FormattedMessage
          defaultMessage='Renovar'
          id='circulation.lending.button.renew'
        />
      </EuiButton>
    )
  }

  return (
    <EuiButton
      fill
      disabled={lendingEligibility !== 'eligible' || isBusy}
      onClick={() => onLend(holdingBag)}
    >
      {lendingEligibility === 'eligible' ? (
        <FormattedMessage
          defaultMessage='Emprestar'
          id='circulation.lending.button.lend'
        />
      ) : (
        <FormattedMessage
          defaultMessage='Indisponível'
          id='circulation.lending.button.unavailable'
        />
      )}
    </EuiButton>
  )
}

const FragmentReservationWarning = () => (
  <Fragment>
    <EuiSpacer size='s' />
    <EuiCallOut
      announceOnMount
      color='warning'
      iconType='warning'
      size='s'
      title={
        <FormattedMessage
          defaultMessage='Atenção: a prioridade de reserva é de outro usuário.'
          id='circulation.lending.reservation.warning'
        />
      }
    />
  </Fragment>
)

export default CirculationLendingView
