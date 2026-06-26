import {
  EuiCallOut,
  EuiCheckbox,
  EuiDatePicker,
  EuiFieldNumber,
  EuiFieldText,
  EuiFormRow,
} from '@elastic/eui'
import moment from 'moment'

import type { Moment } from 'moment'

import type { ReportTemplateParameter } from '../../generated-sources'

const SUPPORTED_PARAMETER_TYPES = new Set([
  'java.lang.String',
  'java.lang.Integer',
  'java.lang.Long',
  'java.lang.Float',
  'java.lang.Double',
  'java.lang.Boolean',
  'java.time.LocalDate',
  'java.time.LocalDateTime',
])

export function isSupportedParameterType(type: string | undefined): boolean {
  return type !== undefined && SUPPORTED_PARAMETER_TYPES.has(type)
}

export function getParameterLabel(parameter: ReportTemplateParameter): string {
  return parameter.description?.trim() || parameter.name || ''
}

export function getShortTypeName(type: string | undefined): string {
  if (type === undefined) {
    return 'desconhecido'
  }

  const lastSegment = type.split('.').pop()

  return lastSegment ?? type
}

type ReportParameterFieldProps = {
  parameter: ReportTemplateParameter
  value: string
  onChange: (name: string, value: string) => void
}

export function ReportParameterField({
  parameter,
  value,
  onChange,
}: ReportParameterFieldProps) {
  const name = parameter.name ?? ''
  const type = parameter.type
  const label = getParameterLabel(parameter)

  if (!isSupportedParameterType(type)) {
    return (
      <EuiCallOut
        color='danger'
        iconType='alert'
        size='s'
        title={`Parâmetro "${name}" usa um tipo não suportado (${type ?? 'desconhecido'})`}
      />
    )
  }

  switch (type) {
    case 'java.lang.Boolean':
      return (
        <EuiFormRow label={label}>
          <EuiCheckbox
            checked={value === 'true'}
            id={`report-parameter-${name}`}
            onChange={(e) => onChange(name, e.target.checked ? 'true' : 'false')}
          />
        </EuiFormRow>
      )
    case 'java.lang.Integer':
    case 'java.lang.Long':
    case 'java.lang.Float':
    case 'java.lang.Double':
      return (
        <EuiFormRow label={label}>
          <EuiFieldNumber
            compressed
            value={value === '' ? undefined : Number(value)}
            onChange={(e) => onChange(name, e.target.value)}
          />
        </EuiFormRow>
      )
    case 'java.time.LocalDate':
      return (
        <EuiFormRow label={label}>
          <EuiDatePicker
            compressed
            locale='pt-br'
            selected={value ? moment(value, 'YYYY-MM-DD') : null}
            onChange={(date: Moment | null) => {
              onChange(name, date ? date.format('YYYY-MM-DD') : '')
            }}
          />
        </EuiFormRow>
      )
    case 'java.time.LocalDateTime':
      return (
        <EuiFormRow label={label}>
          <EuiDatePicker
            compressed
            locale='pt-br'
            selected={value ? moment(value) : null}
            showTimeSelect
            onChange={(date: Moment | null) => {
              onChange(name, date ? date.format('YYYY-MM-DDTHH:mm:ss') : '')
            }}
          />
        </EuiFormRow>
      )
    default:
      return (
        <EuiFormRow label={label}>
          <EuiFieldText
            compressed
            value={value}
            onChange={(e) => onChange(name, e.target.value)}
          />
        </EuiFormRow>
      )
  }
}

export function createInitialParameterValues(
  parameters: ReportTemplateParameter[] | undefined,
): Record<string, string> {
  if (!parameters) {
    return {}
  }

  return parameters.reduce(
    (values, parameter) => {
      if (parameter.name === undefined) {
        return values
      }

      values[parameter.name] =
        parameter.type === 'java.lang.Boolean' ? 'false' : ''

      return values
    },
    {} as Record<string, string>,
  )
}

export function areParameterValuesValid(
  parameters: ReportTemplateParameter[] | undefined,
  values: Record<string, string>,
): boolean {
  if (!parameters || parameters.length === 0) {
    return true
  }

  return parameters.every((parameter) => {
    if (!isSupportedParameterType(parameter.type)) {
      return false
    }

    if (parameter.name === undefined) {
      return false
    }

    if (parameter.type === 'java.lang.Boolean') {
      return true
    }

    return values[parameter.name]?.trim().length > 0
  })
}

export function hasUnsupportedParameterTypes(
  parameters: ReportTemplateParameter[] | undefined,
): boolean {
  return (
    parameters?.some((parameter) => !isSupportedParameterType(parameter.type)) ??
    false
  )
}
