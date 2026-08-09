import { EuiCallOut, EuiSpacer, EuiText } from '@elastic/eui'
import { Fragment } from 'react'
import { FormattedMessage } from 'react-intl'

import type { FC } from 'react'

import type { ReservationInfo } from '../../api-helpers/circulation/response-types'

type Props = {
  reservation: ReservationInfo
}

const NextReservationNotice: FC<Props> = ({ reservation }) => {
  const { user, reservation: reservationInfo } = reservation

  return (
    <Fragment>
      <EuiSpacer size='m' />
      <EuiCallOut
        color='primary'
        iconType='calendar'
        title={
          <FormattedMessage
            defaultMessage='Título reservado'
            id='circulation.return.reservation.title'
          />
        }
      >
        <EuiText size='s'>
          <FormattedMessage
            defaultMessage='Próximo na fila: {name}'
            id='circulation.return.reservation.next'
            values={{
              name: user?.name ?? reservationInfo.userId,
            }}
          />
        </EuiText>
      </EuiCallOut>
    </Fragment>
  )
}

export default NextReservationNotice
