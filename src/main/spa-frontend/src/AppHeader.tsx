import {
  EuiButton,
  EuiButtonEmpty,
  EuiButtonIcon,
  EuiFlexGroup,
  EuiHeader,
  EuiHeaderSection,
  EuiHeaderSectionItem,
  EuiIcon,
  EuiLink,
  EuiText,
} from '@elastic/eui'
import { useState } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'
import { useNavigate } from 'react-router-dom'

import { BIBLIVRE_ENDPOINT } from './api-helpers/constants'
import { useAuthSession, useLegacyLogout } from './api-helpers/login/hooks'
import { useSchemasList } from './api-helpers/schema/hooks'
import { useSchemaQueryParams } from './api-helpers/schema/useSchemaQueryParams'
import LibraryHeading from './components/LibraryHeading'
import messages from './messages'
import SchemaSelectionModal from './SchemaSelectionModal'

import type { FC } from 'react'

import type {
  LoggedLoginSessionResponse,
  LoginSessionResponse,
  SchemaListItem,
} from './api-helpers/types'

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
  const { data: schemas, isPending: isSchemasPending } = useSchemasList()

  const [isSchemaModalOpen, setIsSchemaModalOpen] = useState(false)

  const { activeSchemaId, applyActiveSchema } = useSchemaQueryParams({
    schemas,
    setIsSchemaModalOpen,
  })

  const loggedIn = isLoggedInSession(session)

  const activeSchema = schemas?.find((s) => s.schema === activeSchemaId)

  const handleAuthClick = () => {
    if (loggedIn) {
      logout()
    } else {
      navigate('/spa/login')
    }
  }

  return (
    <>
      <EuiHeader>
        <EuiHeaderSection side='left'>
          <EuiHeaderSectionItem>
            <EuiLink href={getLegacyLibraryUrl(schemas, activeSchemaId)}>
              <EuiFlexGroup alignItems='center' gutterSize='s'>
                <EuiIcon aria-hidden type='chevronSingleLeft' />
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
              {schemas && schemas.length > 0 ? (
                <EuiFlexGroup
                  alignItems='center'
                  gutterSize='xs'
                  responsive={false}
                >
                  {activeSchema || activeSchemaId ? (
                    <LibraryHeading
                      name={activeSchema?.name ?? activeSchemaId}
                      subtitle={activeSchema?.subtitle}
                      subtitleMaxWidth='200px'
                      titleSize='s'
                    />
                  ) : null}
                  {schemas.length > 1 ? (
                    <EuiButtonEmpty
                      isLoading={isSchemasPending}
                      size='s'
                      onClick={() => setIsSchemaModalOpen(true)}
                    >
                      <FormattedMessage
                        defaultMessage='Escolher biblioteca'
                        id='app.header.schema.choose_library'
                      />
                    </EuiButtonEmpty>
                  ) : null}
                </EuiFlexGroup>
              ) : null}
              {loggedIn ? (
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
                    defaultMessage='Entrar no sistema'
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
      {schemas && schemas.length > 1 ? (
        <SchemaSelectionModal
          initialSchemaId={activeSchemaId}
          isOpen={isSchemaModalOpen}
          schemas={schemas}
          onClose={() => setIsSchemaModalOpen(false)}
          onConfirm={(schemaId) => {
            applyActiveSchema(schemaId)
            setIsSchemaModalOpen(false)
            navigate('/spa/search', { replace: true })
          }}
        />
      ) : null}
    </>
  )
}

function getLegacyLibraryUrl(
  schemas: SchemaListItem[] | undefined,
  activeSchemaId: string | null,
): string | undefined {
  if (!schemas || !activeSchemaId) {
    return undefined
  }

  if (schemas.length === 1 && schemas[0].schema === 'single') {
    // TODO: consider the case where single is the single schema activated, but multi-library is enabled
    // In that case, we should return the legacy library url for the single schema
    return `${window.location.origin}/`
  }

  const schema = schemas.find((s) => s.schema === activeSchemaId)

  if (!schema) {
    return undefined
  }

  return `${BIBLIVRE_ENDPOINT}/${schema.schema}`
}

export default AppHeader
