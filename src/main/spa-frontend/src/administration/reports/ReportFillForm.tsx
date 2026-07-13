import {
  EuiButton,
  EuiCallOut,
  EuiFlexGroup,
  EuiForm,
  EuiSpacer,
} from '@elastic/eui'
import { useEffect, useMemo, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import {
  areParameterValuesValid,
  createInitialParameterValues,
  hasUnsupportedParameterTypes,
  ReportParameterField,
} from './reportParameterFields'

import type { FormEvent } from 'react'

import type { ReportFillRequest, ReportTemplate } from '../../generated-sources'

type ReportFillFormProps = {
  report: ReportTemplate
  onSubmit: (report: ReportFillRequest) => void
  pending: boolean
  errorMessage?: string
}

const ReportFillForm = ({
  report,
  onSubmit,
  pending,
  errorMessage,
}: ReportFillFormProps) => {
  const parameters = report.parameters ?? []

  const initialValues = useMemo(
    () => createInitialParameterValues(parameters),
    [report.id, parameters],
  )

  const [parameterValues, setParameterValues] =
    useState<Record<string, string>>(initialValues)

  useEffect(() => {
    setParameterValues(initialValues)
  }, [initialValues])

  const unsupportedTypes = hasUnsupportedParameterTypes(parameters)
  const canSubmit = areParameterValuesValid(parameters, parameterValues)

  const handleParameterChange = (name: string, value: string) => {
    setParameterValues((current) => ({
      ...current,
      [name]: value,
    }))
  }

  const handleSubmit = () => {
    onSubmit({
      reportTemplateId: report.id,
      parameters: parameterValues,
    })
  }

  const handleFormSubmit = (event: FormEvent) => {
    event.preventDefault()

    if (canSubmit && !unsupportedTypes) {
      handleSubmit()
    }
  }

  return (
    <EuiFlexGroup direction='column'>
      {errorMessage && (
        <EuiCallOut
          announceOnMount
          color='danger'
          iconType='error'
          title={
            <FormattedMessage
              defaultMessage='Erro ao gerar relatório'
              id='app.reports.fill.submit.error'
            />
          }
        >
          <p>{errorMessage}</p>
        </EuiCallOut>
      )}
      {parameters.length === 0 && (
        <EuiCallOut
          announceOnMount
          iconType='iInCircle'
          size='s'
          title={
            <FormattedMessage
              defaultMessage='Este modelo não possui parâmetros'
              id='app.reports.fill.submit.no.parameters.info'
            />
          }
        />
      )}
      <EuiForm component='form' onSubmit={handleFormSubmit}>
        {parameters.map((parameter) =>
          parameter.name ? (
            <ReportParameterField
              key={parameter.name}
              parameter={parameter}
              value={parameterValues[parameter.name] ?? ''}
              onChange={handleParameterChange}
            />
          ) : null,
        )}
        {unsupportedTypes && (
          <>
            <EuiSpacer size='m' />
            <EuiCallOut
              announceOnMount
              color='danger'
              iconType='alert'
              title={
                <FormattedMessage
                  defaultMessage='Este relatório possui parâmetros com tipos não suportados e não pode ser gerado pela interface.'
                  id='app.reports.fill.submit.unsupported.info'
                />
              }
            />
          </>
        )}
        {!canSubmit && parameters.length > 0 && !unsupportedTypes && (
          <>
            <EuiSpacer size='m' />
            <EuiCallOut
              announceOnMount
              iconType='info'
              size='s'
              title={
                <FormattedMessage
                  defaultMessage='Preencha todos os parâmetros para gerar o relatório'
                  id='app.reports.fill.submit.info'
                />
              }
            />
          </>
        )}
        <EuiSpacer size='m' />
        <EuiButton
          fill
          disabled={!canSubmit || unsupportedTypes || pending}
          isLoading={pending}
          type='submit'
        >
          <FormattedMessage
            defaultMessage='Gerar relatório'
            id='app.reports.fill.submit'
          />
        </EuiButton>
      </EuiForm>
    </EuiFlexGroup>
  )
}

export default ReportFillForm
