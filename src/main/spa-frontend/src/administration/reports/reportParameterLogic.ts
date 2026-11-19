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

    return values[parameter.name].trim().length > 0
  })
}

export function hasUnsupportedParameterTypes(
  parameters: ReportTemplateParameter[] | undefined,
): boolean {
  return (
    parameters?.some(
      (parameter) => !isSupportedParameterType(parameter.type),
    ) ?? false
  )
}
