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
import { useQueryClient } from '@tanstack/react-query'
import { useEffect, useRef, useState } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'
import { useNavigate } from 'react-router-dom'

import {
  AUTH_SESSION_QUERY_KEY,
  useAuthSession,
  useLegacyLogout,
} from './api-helpers/login/hooks'
import { useSchemasList } from './api-helpers/schema/hooks'
import { getStoredSchema, setStoredSchema } from './api-helpers/schema/storage'
import messages from './messages'
import SchemaSelectionModal from './SchemaSelectionModal'

import type { FC } from 'react'

import type {
  LoggedLoginSessionResponse,
  LoginSessionResponse,
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
  const queryClient = useQueryClient()

  const { data: session, isPending: isSessionPending } = useAuthSession()
  const { mutate: logout, isPending: isLogoutPending } = useLegacyLogout()
  const { data: schemas, isPending: isSchemasPending } = useSchemasList()

  const [activeSchemaId, setActiveSchemaId] = useState<string | null>(() =>
    getStoredSchema(),
  )
  const [isSchemaModalOpen, setIsSchemaModalOpen] = useState(false)
  const schemaAutoPromptedRef = useRef(false)

  useEffect(() => {
    if (!schemas?.length) {
      return
    }
    if (schemas.length === 1) {
      const only = schemas[0].schema
      if (getStoredSchema() !== only) {
        setStoredSchema(only)
        setActiveSchemaId(only)
        void queryClient.invalidateQueries({ queryKey: AUTH_SESSION_QUERY_KEY })
      } else {
        setActiveSchemaId(only)
      }
      return
    }
    setActiveSchemaId(getStoredSchema())
  }, [schemas, queryClient])

  useEffect(() => {
    if (!schemas || schemas.length <= 1 || schemaAutoPromptedRef.current) {
      return
    }
    if (!getStoredSchema()) {
      schemaAutoPromptedRef.current = true
      setIsSchemaModalOpen(true)
    }
  }, [schemas])

  const loggedIn = isLoggedInSession(session)

  const currentLibraryLabel =
    schemas?.find((s) => s.schema === activeSchemaId)?.name ??
    activeSchemaId ??
    null

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
              {schemas && schemas.length > 0 ? (
                <EuiFlexGroup
                  alignItems='center'
                  gutterSize='xs'
                  responsive={false}
                >
                  {currentLibraryLabel ? (
                    <EuiText css={{ fontWeight: 500 }} size='s'>
                      {currentLibraryLabel}
                    </EuiText>
                  ) : null}
                  {schemas.length > 1 ? (
                    <EuiButtonEmpty
                      isLoading={isSchemasPending}
                      size='s'
                      onClick={() => setIsSchemaModalOpen(true)}
                    >
                      <FormattedMessage
                        defaultMessage='Biblioteca'
                        id='app.header.schema.choose_library'
                      />
                    </EuiButtonEmpty>
                  ) : null}
                </EuiFlexGroup>
              ) : null}
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
      {schemas && schemas.length > 1 ? (
        <SchemaSelectionModal
          initialSchemaId={activeSchemaId}
          isOpen={isSchemaModalOpen}
          schemas={schemas}
          onClose={() => setIsSchemaModalOpen(false)}
          onConfirm={(schemaId) => {
            setStoredSchema(schemaId)
            setActiveSchemaId(schemaId)
            setIsSchemaModalOpen(false)
            void queryClient.invalidateQueries()
            navigate('/spa/search', { replace: true })
          }}
        />
      ) : null}
    </>
  )
}

export default AppHeader
