import { EuiBadge, EuiFlexGroup, EuiFlexItem, EuiToolTip } from '@elastic/eui'
import { FormattedMessage, useIntl } from 'react-intl'

import type { FC } from 'react'

const ExperimentalAppBadge: FC = () => {
  const { formatMessage } = useIntl()

  const description = formatMessage({
    defaultMessage:
      'Nova interface web do Biblivre, ainda em desenvolvimento ativo. Nem todas as funcionalidades da versão clássica estão disponíveis, e recursos podem mudar entre versões.',
    id: 'app.experimental.description',
  })

  return (
    <EuiToolTip content={description} position='bottom'>
      <EuiFlexGroup
        alignItems='center'
        aria-label={description}
        component='span'
        gutterSize='none'
        responsive={false}
        tabIndex={0}
      >
        <EuiFlexItem grow={false}>
          <EuiBadge color='accent' iconType='beaker'>
            <FormattedMessage
              defaultMessage='Experimental'
              id='app.experimental.label'
            />
          </EuiBadge>
        </EuiFlexItem>
      </EuiFlexGroup>
    </EuiToolTip>
  )
}

export default ExperimentalAppBadge
