import {
  EuiButtonIcon,
  EuiFlexGroup,
  EuiHeader,
  EuiHeaderSection,
  EuiHeaderSectionItem,
  EuiIcon,
  EuiLink,
} from '@elastic/eui'
import { type FC, useContext } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'

import ColorModeContext from './ColorModeContext'
import messages from './messages'

const AppHeader: FC = () => {
  const { formatMessage } = useIntl()

  const { colorMode, setColorMode } = useContext(ColorModeContext)

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
            iconType={colorMode === 'dark' ? 'sun' : 'moon'}
            onClick={() =>
              setColorMode(colorMode === 'dark' ? 'light' : 'dark')
            }
          />
        </EuiHeaderSectionItem>
      </EuiHeaderSection>
    </EuiHeader>
  )
}

export default AppHeader
