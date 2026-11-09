import {
  EuiBadge,
  EuiButton,
  EuiCallOut,
  EuiEmptyPrompt,
  EuiFieldSearch,
  EuiFlexGroup,
  EuiFlexItem,
  EuiPagination,
  EuiPanel,
  EuiSpacer,
  EuiSwitch,
  EuiText,
  EuiTitle,
  EuiToolTip,
} from '@elastic/eui'
import { Fragment } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'

import HoldingLendingSummary from '../return/HoldingLendingSummary'

import {
  getHoldingStatus,
  isOpenLending,
  isReservedForOtherReaders,
  isUserBlocked,
} from './lendingState'
import { RenewButton } from './ReaderColumn'

import type { FC } from 'react'

import type { HoldingLendingBag } from '../../api-helpers/circulation/response-types'
import type { LendingReader } from '../../api-helpers/lending/response-types'

import type { HoldingStatus, LendBlockReason } from './lendingState'
import type { CirculationLendingState } from './useCirculationLending'

type Props = {
  holdingSearch: CirculationLendingState['holdingSearch']
  reader: LendingReader | null
  blockReason: LendBlockReason | null
  isMutating: boolean
  now: Date
  onLend: (bag: HoldingLendingBag) => void
  onRenew: (bag: HoldingLendingBag) => void
  onReturn: (bag: HoldingLendingBag) => void
}

const HoldingColumn: FC<Props> = ({ reader, ...props }) => (
  <Fragment>
    <EuiTitle size='xs'>
      <h2>
        <FormattedMessage
          defaultMessage='Exemplar'
          id='circulation.lending.page.holding.title'
        />
      </h2>
    </EuiTitle>
    <EuiSpacer size='s' />
    {reader ? (
      <HoldingSearch reader={reader} {...props} />
    ) : (
      <EuiPanel hasBorder>
        <EuiEmptyPrompt
          body={
            <FormattedMessage
              defaultMessage='A busca de exemplares fica disponível depois que você escolhe o leitor.'
              id='circulation.lending.page.holding.empty_body'
            />
          }
          iconType='user'
          title={
            <h3>
              <FormattedMessage
                defaultMessage='Selecione um leitor'
                id='circulation.lending.page.holding.empty_title'
              />
            </h3>
          }
          titleSize='xs'
        />
      </EuiPanel>
    )}
  </Fragment>
)

const HoldingSearch: FC<Omit<Props, 'reader'> & { reader: LendingReader }> = ({
  holdingSearch,
  reader,
  ...cardProps
}) => {
  const { formatMessage } = useIntl()
  const {
    query,
    setQuery,
    onlyLent,
    setOnlyLent,
    results,
    paging,
    emptyMessage,
    isLoading,
    onSearch,
  } = holdingSearch

  return (
    <EuiPanel hasBorder>
      <EuiFieldSearch
        autoFocus
        fullWidth
        isClearable
        aria-label={formatMessage({
          defaultMessage: 'Buscar exemplar',
          id: 'circulation.lending.page.holding.search_label',
        })}
        isLoading={isLoading}
        placeholder={formatMessage({
          defaultMessage: 'Tombo, título ou autor (Enter para buscar)',
          id: 'circulation.lending.page.holding.search_placeholder',
        })}
        value={query}
        onChange={(event) => setQuery(event.target.value)}
        onSearch={() => void onSearch(1)}
      />
      <EuiSpacer size='s' />
      <EuiSwitch
        compressed
        checked={onlyLent}
        label={formatMessage({
          defaultMessage: 'Listar apenas exemplares emprestados',
          id: 'circulation.lending.page.holding.only_lent',
        })}
        onChange={(event) => setOnlyLent(event.target.checked)}
      />
      <EuiSpacer size='m' />
      <HoldingResults
        emptyMessage={emptyMessage}
        paging={paging}
        reader={reader}
        results={results}
        onPageChange={(page) => void onSearch(page)}
        {...cardProps}
      />
    </EuiPanel>
  )
}

const HoldingResults: FC<
  Omit<Props, 'holdingSearch' | 'reader'> & {
    reader: LendingReader
    results: HoldingLendingBag[] | null
    paging: CirculationLendingState['holdingSearch']['paging']
    emptyMessage: string | undefined
    onPageChange: (page: number) => void
  }
> = ({ results, paging, emptyMessage, onPageChange, ...cardProps }) => {
  const { formatMessage } = useIntl()

  if (results === null) {
    return (
      <EuiText color='subdued' size='s'>
        <FormattedMessage
          defaultMessage='Leia o código de barras ou digite o tombo: se apenas um exemplar disponível corresponder, o empréstimo é feito na hora (exceto quando o título está reservado para outros leitores).'
          id='circulation.lending.page.holding.search_hint'
        />
      </EuiText>
    )
  }

  if (results.length === 0) {
    return (
      <EuiText color='subdued' size='s'>
        {emptyMessage ??
          formatMessage({
            defaultMessage: 'Nenhum exemplar encontrado',
            id: 'circulation.lending.page.holding.no_results',
          })}
      </EuiText>
    )
  }

  return (
    <Fragment>
      <EuiText color='subdued' size='xs'>
        <FormattedMessage
          defaultMessage='{count, plural, one {# exemplar} other {# exemplares}}'
          id='circulation.lending.page.holding.result_count'
          values={{ count: paging?.total ?? results.length }}
        />
      </EuiText>
      <EuiSpacer size='s' />
      <EuiFlexGroup direction='column' gutterSize='s'>
        {results.map((bag) => (
          <EuiFlexItem key={bag.holding.id} grow={false}>
            <HoldingCard bag={bag} {...cardProps} />
          </EuiFlexItem>
        ))}
      </EuiFlexGroup>
      {paging && paging.pageCount > 1 && (
        <Fragment>
          <EuiSpacer size='m' />
          <EuiFlexGroup justifyContent='center'>
            <EuiPagination
              activePage={paging.page - 1}
              aria-label={formatMessage({
                defaultMessage: 'Páginas de exemplares',
                id: 'circulation.lending.page.holding.pagination',
              })}
              pageCount={paging.pageCount}
              onPageClick={(index) => onPageChange(index + 1)}
            />
          </EuiFlexGroup>
        </Fragment>
      )}
    </Fragment>
  )
}

const HoldingCard: FC<
  Omit<Props, 'holdingSearch' | 'reader'> & {
    bag: HoldingLendingBag
    reader: LendingReader
  }
> = ({
  bag,
  reader,
  blockReason,
  isMutating,
  now,
  onLend,
  onRenew,
  onReturn,
}) => {
  const status = getHoldingStatus(bag, reader.user.id)
  // Only open lendings matter here; hide the last closed one.
  const summaryBag = isOpenLending(bag.lending)
    ? bag
    : { ...bag, user: undefined, lending: undefined }

  return (
    <EuiPanel hasBorder paddingSize='m'>
      <HoldingLendingSummary holdingLendingBag={summaryBag} />
      {status === 'available' && isReservedForOtherReaders(bag, reader) && (
        <Fragment>
          <EuiSpacer size='s' />
          <ReservedForOtherReadersCallOut
            reservationCount={bag.biblio?.holdings_reserved ?? 0}
          />
        </Fragment>
      )}
      <EuiSpacer size='s' />
      <EuiFlexGroup wrap alignItems='center' gutterSize='s' responsive={false}>
        <EuiFlexItem grow={false}>
          <HoldingStatusBadge status={status} />
        </EuiFlexItem>
        {status === 'available' && (
          <EuiFlexItem grow={false}>
            <LendButton
              bag={bag}
              blockReason={blockReason}
              isDisabled={isMutating}
              onLend={onLend}
            />
          </EuiFlexItem>
        )}
        {(status === 'lent_to_reader' || status === 'lent_to_other') && (
          <Fragment>
            <EuiFlexItem grow={false}>
              <RenewButton
                bag={bag}
                isDisabled={isMutating}
                isReaderBlocked={isUserBlocked(bag.user)}
                now={now}
                onRenew={onRenew}
              />
            </EuiFlexItem>
            <EuiFlexItem grow={false}>
              <EuiButton
                disabled={isMutating}
                size='s'
                onClick={() => onReturn(bag)}
              >
                <FormattedMessage
                  defaultMessage='Devolver'
                  id='circulation.lending.page.button.return'
                />
              </EuiButton>
            </EuiFlexItem>
          </Fragment>
        )}
      </EuiFlexGroup>
    </EuiPanel>
  )
}

const ReservedForOtherReadersCallOut: FC<{ reservationCount: number }> = ({
  reservationCount,
}) => (
  <EuiCallOut
    color='warning'
    iconType='calendar'
    size='s'
    text={
      <p>
        <FormattedMessage
          defaultMessage='{count, plural, one {Há # reserva ativa} other {Há # reservas ativas}} e todos os exemplares disponíveis deste título estão reservados para outros leitores. O empréstimo pode ser feito, mas confira as reservas antes.'
          id='circulation.lending.page.reserved.body'
          values={{ count: reservationCount }}
        />
      </p>
    }
    title={
      <FormattedMessage
        defaultMessage='Título reservado para outros leitores'
        id='circulation.lending.page.reserved.title'
      />
    }
  />
)

const HoldingStatusBadge: FC<{ status: HoldingStatus }> = ({ status }) => {
  switch (status) {
    case 'available':
      return (
        <EuiBadge color='success'>
          <FormattedMessage
            defaultMessage='Disponível'
            id='circulation.lending.page.status.available'
          />
        </EuiBadge>
      )
    case 'lent_to_reader':
      return (
        <EuiBadge color='primary'>
          <FormattedMessage
            defaultMessage='Emprestado a este leitor'
            id='circulation.lending.page.status.lent_to_reader'
          />
        </EuiBadge>
      )
    case 'lent_to_other':
      return (
        <EuiBadge color='warning'>
          <FormattedMessage
            defaultMessage='Emprestado a outro leitor'
            id='circulation.lending.page.status.lent_to_other'
          />
        </EuiBadge>
      )
    case 'unavailable':
      return (
        <EuiBadge color='hollow'>
          <FormattedMessage
            defaultMessage='Indisponível para empréstimo'
            id='circulation.lending.page.status.unavailable'
          />
        </EuiBadge>
      )
  }
}

const LendButton: FC<{
  bag: HoldingLendingBag
  blockReason: LendBlockReason | null
  isDisabled: boolean
  onLend: (bag: HoldingLendingBag) => void
}> = ({ bag, blockReason, isDisabled, onLend }) => {
  const { formatMessage } = useIntl()

  const button = (
    <EuiButton
      fill
      disabled={blockReason !== null || isDisabled}
      size='s'
      onClick={() => onLend(bag)}
    >
      <FormattedMessage
        defaultMessage='Emprestar'
        id='circulation.lending.page.button.lend'
      />
    </EuiButton>
  )

  if (blockReason === null) {
    return button
  }

  return (
    <EuiToolTip
      content={
        blockReason === 'blocked_reader'
          ? formatMessage({
              defaultMessage: 'O leitor está bloqueado ou inativo',
              id: 'circulation.lending.page.lend_disabled.blocked',
            })
          : formatMessage({
              defaultMessage: 'O leitor atingiu o limite de empréstimos',
              id: 'circulation.lending.page.lend_disabled.limit',
            })
      }
    >
      {button}
    </EuiToolTip>
  )
}

export default HoldingColumn
