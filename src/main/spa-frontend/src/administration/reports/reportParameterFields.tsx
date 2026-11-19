import {
  EuiCallOut,
  EuiCheckbox,
  EuiDatePicker,
  EuiFieldNumber,
  EuiFieldText,
  EuiFormRow,
} from '@elastic/eui'
import moment from 'moment'

import { DAY_FIRST_DATE_FORMAT } from '../../constants'

import {
  getParameterLabel,
  isSupportedParameterType,
} from './reportParameterLogic'

import type { Moment } from 'moment'

import type { ReportTemplateParameter } from '../../generated-sources'

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
        announceOnMount
        color='danger'
        iconType='warning'
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
            onChange={(e) =>
              onChange(name, e.target.checked ? 'true' : 'false')
            }
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
            dateFormat={DAY_FIRST_DATE_FORMAT}
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
            showTimeSelect
            dateFormat={DAY_FIRST_DATE_FORMAT}
            locale='pt-br'
            selected={value ? moment(value) : null}
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
