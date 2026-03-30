import {
  EuiButton,
  EuiButtonIcon,
  EuiFlexGroup,
  EuiHeader,
  EuiHeaderSection,
  EuiHeaderSectionItem,
  EuiIcon,
  EuiLink,
  EuiText,
} from '@elastic/eui'
import { FormattedMessage, useIntl } from 'react-intl'
import { useNavigate } from 'react-router-dom'

import { useAuthSession, useLegacyLogout } from './api-helpers/login/hooks'
import messages from './messages'

import type { FC } from 'react'
import type { LoggedLoginSessionResponse, LoginSessionResponse } from './api-helpers/types'


type Props = {
  isDarkMode: boolean
  setIsDarkMode: (isDarkMode: boolean) => void
}

function isLoggedInSession(
  session: LoginSessionResponse | undefined,
): session is LoggedLoginSessionResponse {
  return (
    session?.success === true && 'logged' in session && session.logged === true
  )
}

const AppHeader: FC<Props> = ({ isDarkMode, setIsDarkMode }) => {
  const { formatMessage } = useIntl()
  const navigate = useNavigate()

  const { data: session, isPending: isSessionPending } = useAuthSession()
  const { mutate: logout, isPending: isLogoutPending } = useLegacyLogout()

  const loggedIn = isLoggedInSession(session)

  const handleAuthClick = () => {
    if (loggedIn) {
      logout()
    } else {
      navigate('/spa/login')
    }
  }

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
          <EuiFlexGroup alignItems='center' gutterSize='s' responsive={false}>
            {loggedIn && session?.username ? (
              <EuiText css={{ fontWeight: 500 }} size='s'>
                {session.username}
              </EuiText>
            ) : null}
            <EuiButton
              isLoading={isSessionPending || isLogoutPending}
              size='s'
              onClick={handleAuthClick}
            >
              {loggedIn ? (
                <FormattedMessage
                  defaultMessage='Sair'
                  id='app.header.logout'
                />
              ) : (
                <FormattedMessage
                  defaultMessage='Entrar'
                  id='app.header.login'
                />
              )}
            </EuiButton>
          </EuiFlexGroup>
        </EuiHeaderSectionItem>
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
