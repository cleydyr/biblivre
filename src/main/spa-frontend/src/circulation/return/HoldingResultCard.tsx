import {
  EuiButton,
  EuiCallOut,
  EuiPanel,
  EuiSpacer,
  EuiText,
} from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import { formatCirculationDateTime } from '../lib'

import HoldingLendingSummary from './HoldingLendingSummary'

import type { FC } from 'react'

import type { HoldingLendingBag } from '../../api-helpers/circulation/response-types'

type Props = {
  holdingLendingBag: HoldingLendingBag
  isBusy: boolean
  onReturn: (lendingId: number) => void
}

const HoldingResultCard: FC<Props> = ({
  holdingLendingBag,
  isBusy,
  onReturn,
}) => {
  const { lending, user } = holdingLendingBag

  const openLending =
    lending && lending.returnDate === undefined ? lending : null
  const closedLending =
    lending && lending.returnDate !== undefined ? lending : null

  return (
    <EuiPanel paddingSize='m'>
      <HoldingLendingSummary holdingLendingBag={holdingLendingBag} />
      <EuiSpacer size='s' />
      {openLending ? (
        <EuiButton
          fill
          disabled={isBusy}
          onClick={() => onReturn(openLending.id)}
        >
          <FormattedMessage
            defaultMessage='Devolver'
            id='circulation.return.button.return'
          />
        </EuiButton>
      ) : (
        <EuiCallOut
          announceOnMount
          color='warning'
          size='s'
          title={
            <FormattedMessage
              defaultMessage='Exemplar não está emprestado'
              id='circulation.return.not_on_loan'
            />
          }
        >
          {closedLending && user && (
            <EuiText size='s'>
              <FormattedMessage
                defaultMessage='Última devolução: {user} em {date}'
                id='circulation.return.last_closed_hint'
                values={{
                  user: user.name,
                  date: formatCirculationDateTime(closedLending.returnDate),
                }}
              />
            </EuiText>
          )}
        </EuiCallOut>
      )}
    </EuiPanel>
  )
}

export default HoldingResultCard
