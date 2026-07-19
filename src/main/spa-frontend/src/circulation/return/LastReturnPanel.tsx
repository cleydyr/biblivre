import {
  EuiButton,
  EuiButtonEmpty,
  EuiCallOut,
  EuiFieldText,
  EuiFlexGroup,
  EuiFlexItem,
  EuiHorizontalRule,
  EuiPanel,
  EuiSpacer,
  EuiText,
  EuiTitle,
} from '@elastic/eui'
import { Fragment } from 'react'
import { FormattedMessage } from 'react-intl'

import HoldingLendingSummary from './HoldingLendingSummary'
import NextReservationNotice from './NextReservationNotice'

import type { FC } from 'react'

import type { LastReturnState } from './useCirculationReturn'

type Props = {
  lastReturn: LastReturnState
  undoStillAvailable: boolean
  isBusy: boolean
  adjustFineId: number | null
  setAdjustFineId: (id: number | null) => void
  adjustValue: string
  setAdjustValue: (value: string) => void
  onUndo: () => void
  onPayFine: (fineId: number, exempt?: boolean) => void
  onAdjustFine: () => void
}

const LastReturnPanel: FC<Props> = ({
  lastReturn,
  undoStillAvailable,
  isBusy,
  adjustFineId,
  setAdjustFineId,
  adjustValue,
  setAdjustValue,
  onUndo,
  onPayFine,
  onAdjustFine,
}) => {
  const { holdingLendingBag, nextReservation } = lastReturn
  const fine = holdingLendingBag.lendingFine
  const unpaidFine = fine && !fine.payment

  return (
    <EuiPanel color='success' paddingSize='m'>
      <EuiFlexGroup alignItems='center' justifyContent='spaceBetween'>
        <EuiTitle size='xs'>
          <h2>
            <FormattedMessage
              defaultMessage='Última devolução'
              id='circulation.return.last_return.title'
            />
          </h2>
        </EuiTitle>
        {undoStillAvailable && (
          <EuiButtonEmpty
            color='danger'
            disabled={isBusy}
            iconType='editorUndo'
            onClick={onUndo}
          >
            <FormattedMessage
              defaultMessage='Desfazer'
              id='circulation.return.undo'
            />
          </EuiButtonEmpty>
        )}
      </EuiFlexGroup>
      <EuiSpacer size='s' />
      <HoldingLendingSummary holdingLendingBag={holdingLendingBag} />

      {unpaidFine && (
        <Fragment>
          <EuiHorizontalRule margin='m' />
          <EuiCallOut
            announceOnMount
            color='warning'
            title={
              <FormattedMessage
                defaultMessage='Multa gerada automaticamente'
                id='circulation.return.fine.auto_created'
              />
            }
          >
            <EuiText size='s'>
              <FormattedMessage
                defaultMessage='Valor: {value}'
                id='circulation.return.fine.value'
                values={{ value: fine.value.toFixed(2) }}
              />
            </EuiText>
            <EuiSpacer size='s' />
            <EuiFlexGroup wrap gutterSize='s'>
              <EuiButton
                disabled={isBusy}
                size='s'
                onClick={() => onPayFine(fine.id)}
              >
                <FormattedMessage
                  defaultMessage='Pagar'
                  id='circulation.return.fine.pay'
                />
              </EuiButton>
              <EuiButton
                color='text'
                disabled={isBusy}
                size='s'
                onClick={() => onPayFine(fine.id, true)}
              >
                <FormattedMessage
                  defaultMessage='Dispensar'
                  id='circulation.return.fine.waive'
                />
              </EuiButton>
              <EuiButtonEmpty
                disabled={isBusy}
                size='s'
                onClick={() => {
                  setAdjustFineId(fine.id)
                  setAdjustValue(String(fine.value))
                }}
              >
                <FormattedMessage
                  defaultMessage='Ajustar'
                  id='circulation.return.fine.adjust'
                />
              </EuiButtonEmpty>
            </EuiFlexGroup>
            {adjustFineId === fine.id && (
              <Fragment>
                <EuiSpacer size='s' />
                <EuiFlexGroup alignItems='center' gutterSize='s'>
                  <EuiFlexItem grow={false}>
                    <EuiFieldText
                      compressed
                      disabled={isBusy}
                      value={adjustValue}
                      onChange={(event) => setAdjustValue(event.target.value)}
                    />
                  </EuiFlexItem>
                  <EuiButton disabled={isBusy} size='s' onClick={onAdjustFine}>
                    <FormattedMessage
                      defaultMessage='Salvar valor'
                      id='circulation.return.fine.save_adjust'
                    />
                  </EuiButton>
                </EuiFlexGroup>
              </Fragment>
            )}
          </EuiCallOut>
        </Fragment>
      )}

      {nextReservation && (
        <NextReservationNotice reservation={nextReservation} />
      )}
    </EuiPanel>
  )
}

export default LastReturnPanel
