import {
  EuiButtonIcon,
  EuiFlexGroup,
  EuiHeader,
  EuiHeaderSection,
  EuiHeaderSectionItem,
  EuiIcon,
  EuiLink,
} from '@elastic/eui'
import { FormattedMessage, useIntl } from 'react-intl'

import messages from './messages'

import type { FC } from 'react'

type Props = {
  isDarkMode: boolean
  setIsDarkMode: (isDarkMode: boolean) => void
}

const AppHeader: FC<Props> = ({ isDarkMode, setIsDarkMode }) => {
  const { formatMessage } = useIntl()

  return (
    <EuiHeader>
      <EuiHeaderSection side='left'>
        <EuiHeaderSectionItem>
          <EuiLink href={import.meta.env.VITE_BIBLIVRE_ENDPOINT}>
            <EuiFlexGroup alignItems='center' gutterSize='s'>
              <EuiIcon type='arrowLeft' />
              <FormattedMessage
                defaultMessage='Voltar para a interface clássica'
                id='app.header.back_to_legacy'
              />
            </EuiFlexGroup>
          </EuiLink>
        </EuiHeaderSectionItem>
      </EuiHeaderSection>
      <EuiHeaderSection side='right'>
        <EuiHeaderSectionItem>
          <EuiButtonIcon
            aria-label={formatMessage(messages.darkButtonDescription)}
            iconType={isDarkMode ? 'sun' : 'moon'}
            onClick={() => setIsDarkMode(!isDarkMode)}
          />
        </EuiHeaderSectionItem>
      </EuiHeaderSection>
    </EuiHeader>
  )
}

export default AppHeader
