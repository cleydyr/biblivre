import {
  EuiAvatar,
  EuiBadge,
  EuiBasicTable,
  EuiButton,
  EuiButtonEmpty,
  EuiButtonIcon,
  EuiCallOut,
  EuiDescriptionList,
  EuiFieldSearch,
  EuiFlexGroup,
  EuiFlexItem,
  EuiPagination,
  EuiPanel,
  EuiScreenReaderOnly,
  EuiSkeletonText,
  EuiSpacer,
  EuiText,
  EuiTitle,
  EuiToolTip,
} from '@elastic/eui'
import { Fragment } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'

import { formatCirculationDate, getUserPhotoUrl } from '../lib'
import { UserStatusBadge } from '../UserBadges'

import { getDaysLate, getOpenLendings, isUserBlocked } from './lendingState'

import type { EuiBasicTableColumn } from '@elastic/eui'
import type { FC, MouseEvent } from 'react'

import type { HoldingLendingBag } from '../../api-helpers/circulation/response-types'
import type { LendingReader } from '../../api-helpers/lending/response-types'
import type { UserType } from '../../generated-sources'

import type { LendBlockReason } from './lendingState'
import type { CirculationLendingState } from './useCirculationLending'

type Props = {
  readerSearch: CirculationLendingState['readerSearch']
  reader: LendingReader | null
  userType: UserType | undefined
  pendingFines: number | undefined
  blockReason: LendBlockReason | null
  receiptCount: number
  isMutating: boolean
  now: Date
  onSelect: (reader: LendingReader) => void
  onClear: () => void
  onRenew: (bag: HoldingLendingBag) => void
  onReturn: (bag: HoldingLendingBag) => void
  onPrintReceipt: () => void
}

const ReaderColumn: FC<Props> = ({
  reader,
  readerSearch,
  onSelect,
  ...props
}) => (
  <Fragment>
    <EuiTitle size='xs'>
      <h2>
        <FormattedMessage
          defaultMessage='Leitor'
          id='circulation.lending.page.reader.title'
        />
      </h2>
    </EuiTitle>
    <EuiSpacer size='s' />
    {reader ? (
      <SelectedReaderPanel reader={reader} {...props} />
    ) : (
      <ReaderSearch readerSearch={readerSearch} onSelect={onSelect} />
    )}
  </Fragment>
)

const ReaderSearch: FC<{
  readerSearch: CirculationLendingState['readerSearch']
  onSelect: (reader: LendingReader) => void
}> = ({ readerSearch, onSelect }) => {
  const { formatMessage } = useIntl()
  const {
    query,
    setQuery,
    results,
    paging,
    emptyMessage,
    isLoading,
    onSearch,
  } = readerSearch

  const selectLabel = (name: string) =>
    formatMessage(
      {
        defaultMessage: 'Selecionar {name}',
        id: 'circulation.lending.page.reader.select',
      },
      { name },
    )

  const columns: EuiBasicTableColumn<LendingReader>[] = [
    {
      name: formatMessage({
        defaultMessage: 'Leitor',
        id: 'circulation.lending.page.reader.name',
      }),
      render: ({ user, lendingInfo }: LendingReader) => (
        <EuiFlexGroup direction='column' gutterSize='xs' responsive={false}>
          <EuiFlexItem grow={false}>
            <EuiText size='s'>
              <strong>{user.name}</strong>
            </EuiText>
            <EuiText color='subdued' size='xs'>
              <FormattedMessage
                defaultMessage='{enrollment} · {count, plural, =0 {nenhum empréstimo} one {# empréstimo} other {# empréstimos}}'
                id='circulation.lending.page.reader.summary'
                values={{
                  enrollment: user.enrollment || user.id,
                  count: lendingInfo?.length ?? 0,
                }}
              />
            </EuiText>
          </EuiFlexItem>
          <EuiFlexItem grow={false}>
            <span>
              <UserStatusBadge status={user.status} />
            </span>
          </EuiFlexItem>
        </EuiFlexGroup>
      ),
    },
    {
      align: 'right',
      width: '48px',
      name: (
        <EuiScreenReaderOnly>
          <span>
            {formatMessage({
              defaultMessage: 'Ações',
              id: 'circulation.lending.page.reader.actions',
            })}
          </span>
        </EuiScreenReaderOnly>
      ),
      render: (row: LendingReader) => (
        <EuiToolTip
          disableScreenReaderOutput
          content={selectLabel(row.user.name)}
        >
          <EuiButtonIcon
            aria-label={selectLabel(row.user.name)}
            display='base'
            iconType='chevronSingleRight'
            size='s'
            onClick={(event: MouseEvent) => {
              // The row is clickable too; avoid selecting twice.
              event.stopPropagation()
              onSelect(row)
            }}
          />
        </EuiToolTip>
      ),
    },
  ]

  return (
    <EuiPanel hasBorder>
      <EuiFieldSearch
        autoFocus
        fullWidth
        isClearable
        aria-label={formatMessage({
          defaultMessage: 'Buscar leitor',
          id: 'circulation.lending.page.reader.search_label',
        })}
        isLoading={isLoading}
        placeholder={formatMessage({
          defaultMessage: 'Nome ou matrícula (Enter para buscar)',
          id: 'circulation.lending.page.reader.search_placeholder',
        })}
        value={query}
        onChange={(event) => setQuery(event.target.value)}
        onSearch={() => void onSearch(1)}
      />
      <EuiSpacer size='m' />
      {results === null ? (
        <EuiText color='subdued' size='s'>
          <FormattedMessage
            defaultMessage='Busque pelo nome ou pela matrícula. Deixe em branco para listar todos os leitores.'
            id='circulation.lending.page.reader.search_hint'
          />
        </EuiText>
      ) : (
        <EuiBasicTable
          columns={columns}
          itemId={({ user }) => String(user.id)}
          items={results}
          loading={isLoading}
          noItemsMessage={
            emptyMessage ??
            formatMessage({
              defaultMessage: 'Nenhum leitor encontrado',
              id: 'circulation.lending.page.reader.no_results',
            })
          }
          rowProps={(row) => ({
            title: selectLabel(row.user.name),
            onClick: () => onSelect(row),
          })}
          tableCaption={formatMessage({
            defaultMessage: 'Leitores encontrados',
            id: 'circulation.lending.page.reader.results_caption',
          })}
        />
      )}
      {paging && paging.pageCount > 1 && (
        <Fragment>
          <EuiSpacer size='m' />
          <EuiFlexGroup justifyContent='center'>
            <EuiPagination
              activePage={paging.page - 1}
              aria-label={formatMessage({
                defaultMessage: 'Páginas de leitores',
                id: 'circulation.lending.page.reader.pagination',
              })}
              pageCount={paging.pageCount}
              onPageClick={(index) => void onSearch(index + 1)}
            />
          </EuiFlexGroup>
        </Fragment>
      )}
    </EuiPanel>
  )
}

const SelectedReaderPanel: FC<
  Omit<Props, 'onSelect' | 'reader' | 'readerSearch'> & {
    reader: LendingReader
  }
> = ({
  reader,
  userType,
  pendingFines,
  blockReason,
  receiptCount,
  isMutating,
  now,
  onClear,
  onRenew,
  onReturn,
  onPrintReceipt,
}) => {
  const { formatMessage, formatNumber } = useIntl()
  const { user } = reader
  const openLendings = getOpenLendings(reader)
  const lateCount = openLendings.filter(
    ({ lending }) => lending && getDaysLate(lending, now) > 0,
  ).length
  const isReaderBlocked = isUserBlocked(user)

  const columns: EuiBasicTableColumn<HoldingLendingBag>[] = [
    {
      name: formatMessage({
        defaultMessage: 'Exemplar',
        id: 'circulation.lending.page.lendings.holding',
      }),
      render: ({ biblio, holding }: HoldingLendingBag) => (
        <EuiText size='s'>
          <strong>{biblio?.title}</strong>
          <br />
          {holding.accession_number}
        </EuiText>
      ),
    },
    {
      name: formatMessage({
        defaultMessage: 'Devolução prevista',
        id: 'circulation.lending.page.lendings.expected_return',
      }),
      render: ({ lending }: HoldingLendingBag) => {
        const daysLate = lending ? getDaysLate(lending, now) : 0

        return (
          <EuiFlexGroup direction='column' gutterSize='xs' responsive={false}>
            <EuiFlexItem grow={false}>
              {formatCirculationDate(lending?.expectedReturnDate)}
            </EuiFlexItem>
            {daysLate > 0 && (
              <EuiFlexItem grow={false}>
                <EuiBadge color='danger'>
                  <FormattedMessage
                    defaultMessage='{days, plural, one {# dia} other {# dias}} de atraso'
                    id='circulation.lending.page.lendings.days_late'
                    values={{ days: daysLate }}
                  />
                </EuiBadge>
              </EuiFlexItem>
            )}
          </EuiFlexGroup>
        )
      },
    },
    {
      align: 'right',
      name: formatMessage({
        defaultMessage: 'Ações',
        id: 'circulation.lending.page.lendings.actions',
      }),
      render: (bag: HoldingLendingBag) => (
        <EuiFlexGroup
          gutterSize='s'
          justifyContent='flexEnd'
          responsive={false}
        >
          <EuiFlexItem grow={false}>
            <RenewButton
              bag={bag}
              isDisabled={isMutating}
              isReaderBlocked={isReaderBlocked}
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
        </EuiFlexGroup>
      ),
    },
  ]

  return (
    <EuiPanel hasBorder>
      <EuiFlexGroup alignItems='center' gutterSize='m' responsive={false}>
        <EuiFlexItem grow={false}>
          {user.photo_id ? (
            <EuiAvatar
              imageUrl={getUserPhotoUrl(user.photo_id)}
              name={user.name}
              size='l'
            />
          ) : (
            <EuiAvatar name={user.name} size='l' />
          )}
        </EuiFlexItem>
        <EuiFlexItem>
          <EuiTitle size='s'>
            <h3>{user.name}</h3>
          </EuiTitle>
          <EuiText color='subdued' size='s'>
            {user.enrollment || user.id}
          </EuiText>
        </EuiFlexItem>
        <EuiFlexItem grow={false}>
          <EuiButtonEmpty iconType='cross' size='s' onClick={onClear}>
            <FormattedMessage
              defaultMessage='Trocar leitor'
              id='circulation.lending.page.reader.clear'
            />
          </EuiButtonEmpty>
        </EuiFlexItem>
      </EuiFlexGroup>

      <EuiSpacer size='m' />

      <EuiDescriptionList
        compressed
        listItems={[
          {
            title: formatMessage({
              defaultMessage: 'Tipo',
              id: 'circulation.lending.page.reader.type',
            }),
            description: user.type_name,
          },
          {
            title: formatMessage({
              defaultMessage: 'Situação',
              id: 'circulation.lending.page.reader.status',
            }),
            description: <UserStatusBadge status={user.status} />,
          },
          {
            title: formatMessage({
              defaultMessage: 'Empréstimos',
              id: 'circulation.lending.page.reader.lending_count',
            }),
            description:
              userType?.lendingLimit === undefined
                ? String(openLendings.length)
                : formatMessage(
                    {
                      defaultMessage: '{count} de {limit}',
                      id: 'circulation.lending.page.reader.lending_count_value',
                    },
                    {
                      count: openLendings.length,
                      limit: userType.lendingLimit,
                    },
                  ),
          },
          {
            title: formatMessage({
              defaultMessage: 'Prazo',
              id: 'circulation.lending.page.reader.lending_days',
            }),
            description:
              userType?.lendingTimeLimit === undefined ? (
                <EuiSkeletonText lines={1} size='s' />
              ) : (
                formatMessage(
                  {
                    defaultMessage:
                      '{days, plural, one {# dia} other {# dias}}',
                    id: 'circulation.lending.page.reader.lending_days_value',
                  },
                  { days: userType.lendingTimeLimit },
                )
              ),
          },
          {
            title: formatMessage({
              defaultMessage: 'Multas pendentes',
              id: 'circulation.lending.page.reader.pending_fines',
            }),
            description:
              pendingFines === undefined ? (
                <EuiSkeletonText lines={1} size='s' />
              ) : (
                <EuiText
                  color={pendingFines > 0 ? 'danger' : 'default'}
                  size='s'
                >
                  {formatNumber(pendingFines, {
                    style: 'currency',
                    currency: 'BRL',
                  })}
                </EuiText>
              ),
          },
        ]}
        type='column'
      />

      <ReaderCallOuts
        blockReason={blockReason}
        lateCount={lateCount}
        lendingLimit={userType?.lendingLimit}
      />

      <EuiSpacer size='m' />

      <EuiTitle size='xxs'>
        <h4>
          <FormattedMessage
            defaultMessage='Empréstimos em aberto'
            id='circulation.lending.page.lendings.title'
          />
        </h4>
      </EuiTitle>
      <EuiBasicTable
        columns={columns}
        itemId={({ holding }) => String(holding.id)}
        items={openLendings}
        noItemsMessage={formatMessage({
          defaultMessage: 'Nenhum empréstimo em aberto',
          id: 'circulation.lending.page.lendings.empty',
        })}
        tableCaption={formatMessage({
          defaultMessage: 'Empréstimos em aberto do leitor',
          id: 'circulation.lending.page.lendings.caption',
        })}
      />

      <EuiSpacer size='m' />

      <EuiButton
        disabled={receiptCount === 0 || isMutating}
        iconType='document'
        onClick={onPrintReceipt}
      >
        <FormattedMessage
          defaultMessage='Imprimir recibo ({count})'
          id='circulation.lending.page.button.receipt'
          values={{ count: receiptCount }}
        />
      </EuiButton>
    </EuiPanel>
  )
}

const ReaderCallOuts: FC<{
  blockReason: LendBlockReason | null
  lateCount: number
  lendingLimit: number | undefined
}> = ({ blockReason, lateCount, lendingLimit }) => (
  <Fragment>
    {blockReason === 'blocked_reader' && (
      <Fragment>
        <EuiSpacer size='m' />
        <EuiCallOut
          announceOnMount
          color='danger'
          iconType='error'
          size='s'
          title={
            <FormattedMessage
              defaultMessage='Leitor bloqueado ou inativo: não pode receber nem renovar empréstimos'
              id='circulation.lending.page.callout.blocked'
            />
          }
        />
      </Fragment>
    )}
    {blockReason === 'limit_reached' && (
      <Fragment>
        <EuiSpacer size='m' />
        <EuiCallOut
          announceOnMount
          color='warning'
          iconType='warning'
          size='s'
          title={
            <FormattedMessage
              defaultMessage='Limite de {limit, plural, one {# empréstimo} other {# empréstimos}} atingido'
              id='circulation.lending.page.callout.limit_reached'
              values={{ limit: lendingLimit }}
            />
          }
        />
      </Fragment>
    )}
    {lateCount > 0 && (
      <Fragment>
        <EuiSpacer size='m' />
        <EuiCallOut
          announceOnMount
          color='warning'
          iconType='clock'
          size='s'
          title={
            <FormattedMessage
              defaultMessage='{count, plural, one {# empréstimo em atraso} other {# empréstimos em atraso}}: a renovação fica indisponível até a devolução'
              id='circulation.lending.page.callout.late'
              values={{ count: lateCount }}
            />
          }
        />
      </Fragment>
    )}
  </Fragment>
)

export const RenewButton: FC<{
  bag: HoldingLendingBag
  isReaderBlocked: boolean
  isDisabled: boolean
  now: Date
  onRenew: (bag: HoldingLendingBag) => void
}> = ({ bag, isReaderBlocked, isDisabled, now, onRenew }) => {
  const { formatMessage } = useIntl()
  const isLate = bag.lending ? getDaysLate(bag.lending, now) > 0 : false

  const button = (
    <EuiButton
      disabled={isLate || isReaderBlocked || isDisabled}
      size='s'
      onClick={() => onRenew(bag)}
    >
      <FormattedMessage
        defaultMessage='Renovar'
        id='circulation.lending.page.button.renew'
      />
    </EuiButton>
  )

  if (!isLate && !isReaderBlocked) {
    return button
  }

  return (
    <EuiToolTip
      content={
        isLate
          ? formatMessage({
              defaultMessage: 'Empréstimos em atraso não podem ser renovados',
              id: 'circulation.lending.page.renew_disabled.late',
            })
          : formatMessage({
              defaultMessage: 'Leitores bloqueados não podem renovar',
              id: 'circulation.lending.page.renew_disabled.blocked',
            })
      }
    >
      {button}
    </EuiToolTip>
  )
}

export default ReaderColumn
