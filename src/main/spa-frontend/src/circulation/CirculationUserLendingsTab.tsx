import {
  EuiDescriptionList,
  EuiEmptyPrompt,
  EuiFlexGroup,
  EuiPanel,
  EuiSkeletonLoading,
  EuiText,
  EuiTitle,
} from '@elastic/eui'
import { FormattedMessage, useIntl } from 'react-intl'

import { useCirculationUserTabData } from '../api-helpers/circulation/hooks'
import LoadingState from '../components/LoadingState'
import { element, partition } from '../lib/arrays'

import { formatCirculationDate, formatCirculationDateTime } from './lib'

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
  const { formatMessage } = useIntl()
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
    ...element({
      title: formatMessage({
        defaultMessage: 'Título',
        id: 'search.bibliographic.title',
      }),
      description: biblio.title,
    }).if(biblio.title !== ''),
    ...element({
      title: formatMessage({
        defaultMessage: 'Autor',
        id: 'search.bibliographic.author',
      }),
      description: biblio.author,
    }).if(biblio.author !== ''),
    ...element({
      title: formatMessage({
        defaultMessage: 'Ano de publicação',
        id: 'search.bibliographic.publication_year',
      }),
      description: biblio.publication_year,
    }).if(biblio.publication_year !== ''),
    ...element({
      title: formatMessage({
        defaultMessage: 'Localização',
        id: 'search.bibliographic.shelf_location',
      }),
      description: shelfLocation,
    }).if(shelfLocation !== ''),
    ...element({
      title: formatMessage({
        defaultMessage: 'Tombo patrimonial',
        id: 'search.holding.accession_number',
      }),
      description: holding.accession_number,
    }).if(holding.accession_number !== ''),
    {
      title: formatMessage({
        defaultMessage: 'Data do empréstimo',
        id: 'circulation.lending.lending_date',
      }),
      description: formatCirculationDateTime(created),
    },
    ...element({
      title: formatMessage({
        defaultMessage: 'Data da devolução',
        id: 'circulation.lending.return_date',
      }),
      description: returnDate ? formatCirculationDateTime(returnDate) : '-',
    }).if(returnDate !== undefined),
    {
      title: formatMessage({
        defaultMessage: 'Data prevista para devolução',
        id: 'circulation.lending.expected_return_date',
      }),
      description: formatCirculationDate(expectedReturnDate),
    },
    ...element({
      title: formatMessage({
        defaultMessage: 'Dias de atraso',
        id: 'circulation.lending.days_late',
      }),
      description: (
        <EuiText color='danger' size='s'>
          {daysLate ? String(daysLate) : '-'}
        </EuiText>
      ),
    }).if(returnDate === undefined && daysLate !== undefined && daysLate > 0),
    ...element({
      title: formatMessage({
        defaultMessage: 'Multa estimada',
        id: 'circulation.lending.estimated_fine',
      }),
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
