import { EuiButton, EuiCallOut, EuiFlexGroup, EuiForm, EuiSpacer } from '@elastic/eui'
import { useEffect, useMemo, useState } from 'react'

import type { FormEvent } from 'react'

import {
  ReportParameterField,
  areParameterValuesValid,
  createInitialParameterValues,
  hasUnsupportedParameterTypes,
} from './reportParameterFields'

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
    if (report.id === undefined) {
      return
    }

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
        <EuiCallOut color='danger' iconType='error' title='Erro ao gerar relatório'>
          <p>{errorMessage}</p>
        </EuiCallOut>
      )}
      {parameters.length === 0 && (
        <EuiCallOut
          iconType='iInCircle'
          size='s'
          title='Este modelo não possui parâmetros'
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
              color='danger'
              iconType='alert'
              title='Este relatório possui parâmetros com tipos não suportados e não pode ser gerado pela interface.'
            />
          </>
        )}
        {!canSubmit && parameters.length > 0 && !unsupportedTypes && (
          <>
            <EuiSpacer size='m' />
            <EuiCallOut
              iconType='iInCircle'
              size='s'
              title='Preencha todos os parâmetros para gerar o relatório'
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
          Gerar relatório
        </EuiButton>
      </EuiForm>
    </EuiFlexGroup>
  )
}

export default ReportFillForm
