import moment from 'moment'

import { DAY_FIRST_DATE_FORMAT } from '../constants'
import {
  getLegacyTranslation,
  getLegacyUserFieldTranslation,
} from '../legacy_translations/lib'
import translations from '../legacy_translations/pt-br.json'

import type {
  CirculationUserFieldErrors,
  User,
} from '../api-helpers/circulation/response-types'
import type { CirculationUserSavePayload } from '../api-helpers/circulation/types'
import type { UserSearchableField } from '../generated-sources'

export type CirculationUserFormValues = {
  id: number | null
  name: string
  enrollment: string
  type: number | null
  status: User['status']
  fields: Record<string, string>
  photoData: string | null
  photoPreviewUrl: string | null
}

export const USER_STATUS_OPTIONS: Array<{
  value: User['status']
  labelKey: string
}> = [
  { value: 'active', labelKey: 'circulation.user_status.active' },
  { value: 'inactive', labelKey: 'circulation.user_status.inactive' },
  { value: 'blocked', labelKey: 'circulation.user_status.blocked' },
  {
    value: 'pending_issues',
    labelKey: 'circulation.user_status.pending_issues',
  },
]

export function createEmptyUserFormValues(
  defaultTypeId: number | null = null,
): CirculationUserFormValues {
  return {
    id: null,
    name: '',
    enrollment: '',
    type: defaultTypeId,
    status: 'active',
    fields: {},
    photoData: null,
    photoPreviewUrl: null,
  }
}

export function userToFormValues(user: User): CirculationUserFormValues {
  const fields: Record<string, string> = {}

  for (const [key, value] of Object.entries(user.fields)) {
    if (value === '') {
      continue
    }

    fields[key] = formatFieldValueForForm(String(value))
  }

  return {
    id: user.id,
    name: user.name.trim(),
    enrollment: user.enrollment || String(user.id),
    type: user.type,
    status: user.status,
    fields,
    photoData: null,
    photoPreviewUrl: user.photo_id ?? null,
  }
}

export function buildSavePayload(
  values: CirculationUserFormValues,
  saveAsNew: boolean,
): CirculationUserSavePayload {
  const payload: CirculationUserSavePayload = {
    id: saveAsNew || values.id == null ? 0 : values.id,
    name: values.name.trim(),
    type: values.type ?? 0,
    status: values.status,
  }

  if (values.photoData) {
    payload.photo_data = values.photoData
  }

  for (const [key, value] of Object.entries(values.fields)) {
    payload[key] = value
  }

  return payload
}

export function validateUserFormValues(
  values: CirculationUserFormValues,
  fields: UserSearchableField[],
): CirculationUserFieldErrors {
  const errors: CirculationUserFieldErrors = {}

  if (!values.name.trim()) {
    errors.name = 'field.error.required'
  } else if (values.name.includes(':')) {
    errors.name = 'circulation.error.invalid_user_name'
  }

  if (values.type == null) {
    errors.type = 'field.error.required'
  }

  for (const field of fields) {
    const value = values.fields[field.key] ?? ''

    if (field.required && !value.trim()) {
      errors[field.key] = 'field.error.required'
      continue
    }

    if (field.maxLength > 0 && value.length > field.maxLength) {
      errors[field.key] = `field.error.max_length:::${field.maxLength}`
      continue
    }

    if (field.type === 'number' && value.trim() && !/^\d+$/.test(value)) {
      errors[field.key] = 'field.error.digits_only'
      continue
    }

    if (
      (field.type === 'date' || field.type === 'datetime') &&
      value.trim() &&
      !moment(value, DAY_FIRST_DATE_FORMAT, true).isValid()
    ) {
      errors[field.key] = `field.error.date:::${DAY_FIRST_DATE_FORMAT}`
    }
  }

  return errors
}

export function getListFieldOptions(fieldKey: string, maxLength: number) {
  const options: Array<{ value: string; text: string }> = []

  for (let index = 1; index <= maxLength; index += 1) {
    options.push({
      value: String(index),
      text: getLegacyUserFieldTranslation(`${fieldKey}.${index}`),
    })
  }

  return options
}

export function parseServerFieldErrors(
  errors: CirculationUserFieldErrors[] | undefined,
): CirculationUserFieldErrors {
  if (!errors) {
    return {}
  }

  return errors.reduce<CirculationUserFieldErrors>(
    (accumulator, errorObject) => {
      for (const [key, message] of Object.entries(errorObject)) {
        accumulator[key] = message
      }

      return accumulator
    },
    {},
  )
}

export function areFormValuesDirty(
  currentValues: CirculationUserFormValues,
  initialValues: CirculationUserFormValues,
): boolean {
  return JSON.stringify(currentValues) !== JSON.stringify(initialValues)
}

export function formatFieldValueForForm(value: string): string {
  if (moment(value, moment.ISO_8601, true).isValid()) {
    return moment(value).format(DAY_FIRST_DATE_FORMAT)
  }

  return value
}

export function formatFieldValueForDisplay(
  field: UserSearchableField,
  value: string | undefined,
): string {
  if (!value?.trim()) {
    return '-'
  }

  switch (field.type) {
    case 'list':
      return getLegacyUserFieldTranslation(`${field.key}.${value}`)
    case 'boolean':
      return value === 'true'
        ? getLegacyTranslation('common.yes')
        : getLegacyTranslation('common.no')
    case 'date':
    case 'datetime':
      return formatFieldValueForForm(value)
    default:
      return value
  }
}

const ACCEPTED_PHOTO_TYPES = ['image/jpeg', 'image/png', 'image/gif']
const MAX_PHOTO_DIMENSION = 800

export function isAcceptedPhotoType(file: File): boolean {
  return ACCEPTED_PHOTO_TYPES.includes(file.type)
}

export async function convertImageFileToJpegBase64(
  file: File,
): Promise<{ base64: string; previewUrl: string }> {
  const objectUrl = URL.createObjectURL(file)

  try {
    const image = await loadImage(objectUrl)
    const canvas = document.createElement('canvas')
    const context = canvas.getContext('2d')

    if (!context) {
      throw new Error('Canvas is not supported')
    }

    const scale = Math.min(
      1,
      MAX_PHOTO_DIMENSION / Math.max(image.width, image.height),
    )
    canvas.width = Math.round(image.width * scale)
    canvas.height = Math.round(image.height * scale)
    context.drawImage(image, 0, 0, canvas.width, canvas.height)

    const dataUrl = canvas.toDataURL('image/jpeg', 0.9)
    const base64 = dataUrl.split(',')[1] ?? ''

    return {
      base64,
      previewUrl: dataUrl,
    }
  } finally {
    URL.revokeObjectURL(objectUrl)
  }
}

function loadImage(src: string): Promise<HTMLImageElement> {
  return new Promise((resolve, reject) => {
    const image = new Image()
    image.onload = () => resolve(image)
    image.onerror = () => reject(new Error('Failed to load image'))
    image.src = src
  })
}

export function getFieldErrorMessage(
  errorKey: string | undefined,
): string | undefined {
  if (!errorKey) {
    return undefined
  }

  if (errorKey.startsWith('field.error.max_length:::')) {
    const maxLength = errorKey.split(':::')[1]

    return getLegacyTranslation('field.error.max_length').replace(
      '{0}',
      maxLength,
    )
  }

  if (errorKey.startsWith('field.error.date:::')) {
    const format = errorKey.split(':::')[1]

    return getLegacyTranslation('field.error.date').replace('{0}', format)
  }

  if (errorKey in translations) {
    return getLegacyTranslation(errorKey as keyof typeof translations)
  }

  return errorKey
}
