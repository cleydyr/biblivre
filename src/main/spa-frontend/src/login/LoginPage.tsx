import {
  EuiButton,
  EuiCallOut,
  EuiFieldPassword,
  EuiFieldText,
  EuiForm,
  EuiFormRow,
  EuiPageTemplate,
  EuiPanel,
  EuiSpacer,
  useEuiTheme,
} from '@elastic/eui'
import { useState } from 'react'
import { FormattedMessage } from 'react-intl'
import { useNavigate } from 'react-router-dom'

import { useLegacyLogin } from '../api-helpers/login/hooks'

import type { FormEvent } from 'react'
import { useQueryClient } from '@tanstack/react-query'

const LoginPage = () => {
  const { euiTheme } = useEuiTheme()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')

  const queryClient = useQueryClient()

  const { mutate, isPending, isError, error } = useLegacyLogin({
    onSuccess: () => {
      navigate('/spa/search')
      queryClient.invalidateQueries()
    },
  })

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    mutate({ username, password })
  }

  return (
    <EuiPageTemplate
      grow={false}
      paddingSize='xl'
      restrictWidth={euiTheme.breakpoint.s}
    >
      <EuiPageTemplate.Header
        pageTitle={
          <FormattedMessage defaultMessage='Entrar' id='app.login.pageTitle' />
        }
      />

      <EuiPageTemplate.Section>
        <EuiPanel hasBorder paddingSize='l'>
          {isError ? (
            <>
              <EuiCallOut
                color='danger'
                iconType='error'
                title={
                  <FormattedMessage
                    defaultMessage='Não foi possível entrar'
                    id='app.login.errorTitle'
                  />
                }
              >
                {error.message}
              </EuiCallOut>
              <EuiSpacer size='m' />
            </>
          ) : null}

          <EuiForm component='form' onSubmit={handleSubmit}>
            <EuiFormRow
              label={
                <FormattedMessage
                  defaultMessage='Usuário'
                  id='app.login.field.username'
                />
              }
            >
              <EuiFieldText
                autoComplete='username'
                disabled={isPending}
                name='username'
                value={username}
                onChange={(e) => {
                  setUsername(e.target.value)
                }}
              />
            </EuiFormRow>

            <EuiFormRow
              label={
                <FormattedMessage
                  defaultMessage='Senha'
                  id='app.login.field.password'
                />
              }
            >
              <EuiFieldPassword
                autoComplete='current-password'
                disabled={isPending}
                name='password'
                type='dual'
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value)
                }}
              />
            </EuiFormRow>

            <EuiSpacer size='m' />

            <EuiButton
              fill
              disabled={isPending}
              isLoading={isPending}
              type='submit'
            >
              <FormattedMessage defaultMessage='Entrar' id='app.login.submit' />
            </EuiButton>
          </EuiForm>
        </EuiPanel>
      </EuiPageTemplate.Section>
    </EuiPageTemplate>
  )
}

export default LoginPage
