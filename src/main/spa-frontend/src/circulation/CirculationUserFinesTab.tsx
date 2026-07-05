import {
  EuiBadge,
  EuiDescriptionList,
  EuiEmptyPrompt,
  EuiFlexGroup,
  EuiPanel,
  EuiSkeletonLoading,
} from '@elastic/eui'
import { FormattedMessage, useIntl } from 'react-intl'

import { useCirculationUserTabData } from '../api-helpers/circulation/hooks'
import LoadingState from '../components/LoadingState'
import { element } from '../lib/arrays'

import { formatCirculationDate } from './lib'

import type { FC } from 'react'

import type { LendingFine } from '../api-helpers/circulation/response-types'
import type { EuiDescriptionListItem } from '../components/types'

type Props = {
  userId: number
}

const CirculationUserFinesTab: FC<Props> = ({ userId }) => {
  const { data, isFetching, isError, isSuccess } = useCirculationUserTabData(
    userId,
    'fines',
    true,
  )

  if (isError) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Ocorreu um erro ao carregar as multas'
            id='circulation.user.flyout.fines.error'
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
      loadedContent={isSuccess && <FinesList fines={data.data.data ?? []} />}
      loadingContent={<LoadingState />}
    />
  )
}

const FinesList: FC<{ fines: LendingFine[] }> = ({ fines }) => {
  if (fines.length === 0) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Este usuário não possui multas'
            id='circulation.user.no_fines'
          />
        }
        iconType='documents'
      />
    )
  }

  return (
    <EuiFlexGroup direction='column' gutterSize='m'>
      {fines.map((fine) => (
        <FineCard key={fine.id} fine={fine} />
      ))}
    </EuiFlexGroup>
  )
}

const FineCard: FC<{ fine: LendingFine }> = ({ fine }) => {
  const { formatMessage } = useIntl()

  const listItems: Array<EuiDescriptionListItem> = [
    ...element({
      title: formatMessage({
        defaultMessage: 'Título',
        id: 'search.bibliographic.title',
      }),
      description: fine.title,
    }).if(fine.title !== ''),
    ...element({
      title: formatMessage({
        defaultMessage: 'Autor',
        id: 'search.bibliographic.author',
      }),
      description: fine.author,
    }).if(fine.author !== ''),
    {
      title: formatMessage({
        defaultMessage: 'Valor da multa',
        id: 'circulation.lending.fine_value',
      }),
      description: fine.value.toFixed(2),
    },
    fine.payment
      ? {
          title: formatMessage({
            defaultMessage: 'Data do pagamento',
            id: 'circulation.lending.payment_date',
          }),
          description: formatCirculationDate(fine.payment),
        }
      : {
          title: formatMessage({
            defaultMessage: 'Situação',
            id: 'circulation.user.flyout.fine.status',
          }),
          description: (
            <EuiBadge color='warning'>
              <FormattedMessage
                defaultMessage='Pendente'
                id='circulation.user.fine.pending'
              />
            </EuiBadge>
          ),
        },
  ]

  return (
    <EuiPanel hasBorder paddingSize='m'>
      <EuiDescriptionList compressed listItems={listItems} type='column' />
    </EuiPanel>
  )
}

export default CirculationUserFinesTab
