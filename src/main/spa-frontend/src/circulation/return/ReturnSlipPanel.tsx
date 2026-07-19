import {
  EuiButton,
  EuiButtonEmpty,
  EuiFlexGroup,
  EuiPanel,
  EuiSpacer,
  EuiText,
  EuiTitle,
} from '@elastic/eui'
import { Fragment } from 'react'
import { FormattedMessage } from 'react-intl'

import type { FC } from 'react'

import type { ReturnSlipItem } from './useCirculationReturn'

type Props = {
  slip: ReturnSlipItem[]
  isBusy: boolean
  onPrintSlip: () => void
  onClearSlip: () => void
}

const ReturnSlipPanel: FC<Props> = ({
  slip,
  isBusy,
  onPrintSlip,
  onClearSlip,
}) => (
  <EuiPanel paddingSize='m'>
    <EuiFlexGroup alignItems='center' justifyContent='spaceBetween'>
      <EuiTitle size='xs'>
        <h2>
          <FormattedMessage
            defaultMessage='Comprovante da visita ({count})'
            id='circulation.return.slip.title'
            values={{ count: slip.length }}
          />
        </h2>
      </EuiTitle>
      <EuiFlexGroup gutterSize='s' justifyContent='flexEnd'>
        <EuiButtonEmpty
          disabled={slip.length === 0 || isBusy}
          onClick={onClearSlip}
        >
          <FormattedMessage
            defaultMessage='Limpar'
            id='circulation.return.slip.clear'
          />
        </EuiButtonEmpty>
        <EuiButton
          disabled={slip.length === 0 || isBusy}
          iconType='printer'
          onClick={onPrintSlip}
        >
          <FormattedMessage
            defaultMessage='Imprimir'
            id='circulation.return.slip.print'
          />
        </EuiButton>
      </EuiFlexGroup>
    </EuiFlexGroup>
    {slip.length === 0 ? (
      <Fragment>
        <EuiSpacer size='s' />
        <EuiText color='subdued' size='s'>
          <FormattedMessage
            defaultMessage='As devoluções desta visita aparecem aqui para impressão.'
            id='circulation.return.slip.empty'
          />
        </EuiText>
      </Fragment>
    ) : (
      <Fragment>
        <EuiSpacer size='m' />
        <EuiFlexGroup direction='column' gutterSize='s'>
          {slip.map((item) => (
            <EuiText key={item.lendingId} size='s'>
              {item.bag.holding.accession_number}
              {item.bag.biblio?.title ? ` — ${item.bag.biblio.title}` : ''}
              {item.bag.user?.name ? ` (${item.bag.user.name})` : ''}
            </EuiText>
          ))}
        </EuiFlexGroup>
      </Fragment>
    )}
  </EuiPanel>
)

export default ReturnSlipPanel
