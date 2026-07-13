import {
  EuiDescriptionList,
  EuiEmptyPrompt,
  EuiFlexGroup,
  EuiPanel,
  EuiSkeletonLoading,
  EuiText,
  EuiTitle,
} from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import { useCirculationUserTabData } from '../api-helpers/circulation/hooks'
import LoadingState from '../components/LoadingState'
import { element, partition } from '../lib/arrays'

import {
  formatCirculationDate,
  formatCirculationDateTime,
  useBiblioDescriptionListItems,
} from './lib'

import type { FC, ReactNode } from 'react'

import type { LendingBag } from '../api-helpers/circulation/response-types'
import type { EuiDescriptionListItem } from '../components/types'

type Props = {
  userId: number
}

const CirculationUserLendingsTab: FC<Props> = ({ userId }) => {
  const { data, isFetching, isError, isSuccess } = useCirculationUserTabData(
    userId,
    'lendings',
    true,
  )

  if (isError) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Ocorreu um erro ao carregar os empréstimos'
            id='circulation.user.flyout.lendings.error'
          />
        }
        color='danger'
        iconType='error'
      />
    )
  }

  return (
    <EuiSkeletonLoading
      isLoading={isFetching}
      loadedContent={
        isSuccess && <LendingsList lendingBags={data.data.data ?? []} />
      }
      loadingContent={<LoadingState />}
    />
  )
}

const LendingsList: FC<{ lendingBags: LendingBag[] }> = ({ lendingBags }) => {
  if (lendingBags.length === 0) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Este usuário não possui empréstimos'
            id='circulation.user.no_lendings'
          />
        }
        iconType='documents'
      />
    )
  }

  const [returnedLendings, activeLendings] = partition(
    lendingBags,
    (lendingBag) => lendingBag.lending.returnDate !== undefined,
  )

  return (
    <EuiFlexGroup direction='column'>
      <LendingSection
        lendingBags={activeLendings}
        title={
          <FormattedMessage
            defaultMessage='Empréstimos ativos'
            id='circulation.user.active_lendings'
          />
        }
      />
      <LendingSection
        lendingBags={returnedLendings}
        title={
          <FormattedMessage
            defaultMessage='Empréstimos devolvidos'
            id='circulation.user.returned_lendings'
          />
        }
      />
    </EuiFlexGroup>
  )
}

const LendingSection: FC<{
  lendingBags: LendingBag[]
  title: ReactNode
}> = ({ lendingBags, title }) => {
  if (lendingBags.length === 0) {
    return null
  }

  return (
    <EuiFlexGroup direction='column' gutterSize='m'>
      <EuiTitle size='xs'>
        <h3>{title}</h3>
      </EuiTitle>
      {lendingBags.map((lendingBag) => (
        <LendingCard key={lendingBag.id} lendingBag={lendingBag} />
      ))}
    </EuiFlexGroup>
  )
}

const LendingCard: FC<{ lendingBag: LendingBag }> = ({ lendingBag }) => {
  const {
    biblio,
    holding,
    lending: {
      created,
      returnDate,
      expectedReturnDate,
      daysLate,
      estimatedFine,
    },
  } = lendingBag
  const shelfLocation = [biblio.shelf_location, holding.location_d]
    .filter(Boolean)
    .join(' ')

  const listItems: Array<EuiDescriptionListItem> = [
    ...useBiblioDescriptionListItems(biblio, shelfLocation),
    ...element({
      title: (
        <FormattedMessage
          defaultMessage='Tombo patrimonial'
          id='search.holding.accession_number'
        />
      ),
      description: holding.accession_number,
    }).if(holding.accession_number !== ''),
    {
      title: (
        <FormattedMessage
          defaultMessage='Data do empréstimo'
          id='circulation.lending.lending_date'
        />
      ),
      description: formatCirculationDateTime(created),
    },
    ...element({
      title: (
        <FormattedMessage
          defaultMessage='Data da devolução'
          id='circulation.lending.return_date'
        />
      ),
      description: returnDate ? formatCirculationDateTime(returnDate) : '-',
    }).if(returnDate !== undefined),
    {
      title: (
        <FormattedMessage
          defaultMessage='Data prevista para devolução'
          id='circulation.lending.expected_return_date'
        />
      ),
      description: formatCirculationDate(expectedReturnDate),
    },
    ...element({
      title: (
        <FormattedMessage
          defaultMessage='Dias de atraso'
          id='circulation.lending.days_late'
        />
      ),
      description: (
        <EuiText color='danger' size='s'>
          {daysLate ? String(daysLate) : '-'}
        </EuiText>
      ),
    }).if(returnDate === undefined && daysLate !== undefined && daysLate > 0),
    ...element({
      title: (
        <FormattedMessage
          defaultMessage='Multa estimada'
          id='circulation.lending.estimated_fine'
        />
      ),
      description: (
        <EuiText color='danger' size='s'>
          {estimatedFine ? estimatedFine.toFixed(2) : '-'}
        </EuiText>
      ),
    }).if(
      returnDate === undefined &&
        estimatedFine !== undefined &&
        estimatedFine > 0,
    ),
  ]

  return (
    <EuiPanel hasBorder paddingSize='m'>
      <EuiDescriptionList compressed listItems={listItems} type='column' />
    </EuiPanel>
  )
}

export default CirculationUserLendingsTab
