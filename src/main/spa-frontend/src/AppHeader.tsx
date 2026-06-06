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
import { useCallback, useEffect, useRef, useState } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'
import { useNavigate, useSearchParams } from 'react-router-dom'

import {
  AUTH_SESSION_QUERY_KEY,
  useAuthSession,
  useLegacyLogout,
} from './api-helpers/login/hooks'
import { useSchemasList } from './api-helpers/schema/hooks'
import {
  clearStoredSchema,
  getStoredSchema,
  setStoredSchema,
} from './api-helpers/schema/storage'
import messages from './messages'
import SchemaSelectionModal from './SchemaSelectionModal'

import type { FC } from 'react'

import type {
  LoggedLoginSessionResponse,
  LoginSessionResponse,
} from './api-helpers/types'

const SCHEMA_QUERY_PARAM = 'schema'
const SHOW_SELECT_SCHEMA_QUERY_PARAM = 'showSelectSchema'

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
  const [searchParams, setSearchParams] = useSearchParams()
  const schemaFromQuery = searchParams.get(SCHEMA_QUERY_PARAM)
  const showSelectSchemaFromQuery = searchParams.has(
    SHOW_SELECT_SCHEMA_QUERY_PARAM,
  )

  const { data: session, isPending: isSessionPending } = useAuthSession()
  const { mutate: logout, isPending: isLogoutPending } = useLegacyLogout()
  const { data: schemas, isPending: isSchemasPending } = useSchemasList()

  const [activeSchemaId, setActiveSchemaId] = useState<string | null>(() =>
    getStoredSchema(),
  )
  const [isSchemaModalOpen, setIsSchemaModalOpen] = useState(false)
  const schemaAutoPromptedRef = useRef(false)

  const applyActiveSchema = useCallback(
    (schemaId: string) => {
      setStoredSchema(schemaId)
      setActiveSchemaId(schemaId)
      void queryClient.invalidateQueries()
    },
    [queryClient],
  )

  const removeQueryParams = useCallback(
    (...keys: string[]) => {
      const next = new URLSearchParams(searchParams)
      let changed = false

      for (const key of keys) {
        if (next.has(key)) {
          next.delete(key)
          changed = true
        }
      }

      if (changed) {
        setSearchParams(next, { replace: true })
      }
    },
    [searchParams, setSearchParams],
  )

  useEffect(() => {
    if (!schemas?.length) {
      return
    }

    if (showSelectSchemaFromQuery) {
      clearStoredSchema()
      setActiveSchemaId(null)
      void queryClient.invalidateQueries()
      schemaAutoPromptedRef.current = true
      removeQueryParams(SHOW_SELECT_SCHEMA_QUERY_PARAM, SCHEMA_QUERY_PARAM)

      if (schemas.length > 1) {
        setIsSchemaModalOpen(true)
        return
      }
    }

    if (schemaFromQuery) {
      const matchedSchema = schemas.find((s) => s.schema === schemaFromQuery)
      if (matchedSchema) {
        schemaAutoPromptedRef.current = true
        applyActiveSchema(matchedSchema.schema)
        removeQueryParams(SCHEMA_QUERY_PARAM)
        return
      }
    }

    if (schemas.length === 1) {
      const only = schemas[0].schema

      if (getStoredSchema() !== only) {
        setStoredSchema(only)
        void queryClient.invalidateQueries({ queryKey: AUTH_SESSION_QUERY_KEY })
      }

      setActiveSchemaId(only)

      return
    }

    setActiveSchemaId(getStoredSchema())
  }, [
    applyActiveSchema,
    queryClient,
    removeQueryParams,
    schemaFromQuery,
    schemas,
    showSelectSchemaFromQuery,
  ])

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
                <EuiIcon type='chevronSingleLeft' />
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
                        defaultMessage='Escolher biblioteca'
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
            applyActiveSchema(schemaId)
            setIsSchemaModalOpen(false)
            navigate('/spa/search', { replace: true })
          }}
        />
      ) : null}
    </>
  )
}

export default AppHeader
