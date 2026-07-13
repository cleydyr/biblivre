import {
  EuiDescriptionList,
  EuiEmptyPrompt,
  EuiFlexGroup,
  EuiPanel,
  EuiSkeletonLoading,
} from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import { useCirculationUserTabData } from '../api-helpers/circulation/hooks'
import LoadingState from '../components/LoadingState'

import {
  formatCirculationDate,
  formatCirculationDateTime,
  useBiblioDescriptionListItems,
} from './lib'

import type { FC } from 'react'

import type { ReservationBag } from '../api-helpers/circulation/response-types'
import type { EuiDescriptionListItem } from '../components/types'

type Props = {
  userId: number
}

const CirculationUserReservationsTab: FC<Props> = ({ userId }) => {
  const { data, isFetching, isError, isSuccess } = useCirculationUserTabData(
    userId,
    'reservations',
    true,
  )

  if (isError) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Ocorreu um erro ao carregar as reservas'
            id='circulation.user.flyout.reservations.error'
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
        isSuccess &&
        data.data.data !== undefined && (
          <ReservationsList
            reservationBags={data.data.data[0]?.reservationInfoList ?? []}
          />
        )
      }
      loadingContent={<LoadingState />}
    />
  )
}

const ReservationsList: FC<{
  reservationBags: ReservationBag[]
}> = ({ reservationBags }) => {
  if (reservationBags.length === 0) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Este usuário não possui reservas'
            id='circulation.user.no_reserves'
          />
        }
        iconType='documents'
      />
    )
  }

  return (
    <EuiFlexGroup direction='column' gutterSize='m'>
      {reservationBags.map((reservationBag) => (
        <ReservationCard
          key={reservationBag.reservation.id}
          reservationBag={reservationBag}
        />
      ))}
    </EuiFlexGroup>
  )
}

const ReservationCard: FC<{ reservationBag: ReservationBag }> = ({
  reservationBag,
}) => {
  const { biblio, reservation } = reservationBag

  const listItems: Array<EuiDescriptionListItem> = [
    ...useBiblioDescriptionListItems(biblio),
    {
      title: (
        <FormattedMessage
          defaultMessage='Data da reserva'
          id='circulation.reservation.reserve_date'
        />
      ),
      description: formatCirculationDateTime(reservation.created),
    },
    {
      title: (
        <FormattedMessage
          defaultMessage='Data de expiração'
          id='circulation.reservation.expiration_date'
        />
      ),
      description: formatCirculationDate(reservation.expires),
    },
  ]

  return (
    <EuiPanel hasBorder paddingSize='m'>
      <EuiDescriptionList compressed listItems={listItems} type='column' />
    </EuiPanel>
  )
}

export default CirculationUserReservationsTab
