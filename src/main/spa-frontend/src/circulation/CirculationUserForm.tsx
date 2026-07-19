import {
  EuiAvatar,
  EuiDatePicker,
  EuiFieldNumber,
  EuiFieldText,
  EuiFilePicker,
  EuiForm,
  EuiFormRow,
  EuiSelect,
  EuiSwitch,
  EuiTextArea,
} from '@elastic/eui'
import moment from 'moment'
import { Fragment } from 'react'

import TypedEuiSelect from '../components/TypedEuiSelect'
import { DAY_FIRST_DATE_FORMAT } from '../constants'
import {
  getLegacyTranslation,
  getLegacyUserFieldTranslation,
} from '../legacy_translations/lib'

import {
  getFieldErrorMessage,
  getListFieldOptions,
  USER_STATUS_OPTIONS,
} from './circulationUserFormLogic'
import { getUserPhotoUrl } from './lib'

import type { FC } from 'react'

import type { CirculationUserFieldErrors } from '../api-helpers/circulation/response-types'
import type { UserSearchableField, UserType } from '../generated-sources'

import type { CirculationUserFormValues } from './circulationUserFormLogic'

type Props = {
  mode: 'create' | 'edit'
  values: CirculationUserFormValues
  fields: UserSearchableField[]
  userTypes: UserType[]
  fieldErrors: CirculationUserFieldErrors
  existingPhotoId?: string
  isPhotoLoading?: boolean
  onChange: (values: CirculationUserFormValues) => void
  onPhotoSelect: (file: File | null) => void
}

const CirculationUserForm: FC<Props> = ({
  mode,
  values,
  fields,
  userTypes,
  fieldErrors,
  existingPhotoId,
  isPhotoLoading = false,
  onChange,
  onPhotoSelect,
}) => {
  const photoPreviewUrl =
    values.photoPreviewUrl ??
    (existingPhotoId ? getUserPhotoUrl(existingPhotoId) : undefined)

  const updateFieldValue = (key: string, value: string) => {
    onChange({
      ...values,
      fields: {
        ...values.fields,
        [key]: value,
      },
    })
  }

  return (
    <EuiForm component='div'>
      <EuiFormRow
        error={getFieldErrorMessage(fieldErrors.name)}
        isInvalid={Boolean(fieldErrors.name)}
        label={getLegacyUserFieldTranslation('name')}
      >
        <EuiFieldText
          isInvalid={Boolean(fieldErrors.name)}
          maxLength={512}
          value={values.name}
          onChange={(event) =>
            onChange({ ...values, name: event.target.value })
          }
        />
      </EuiFormRow>

      {mode === 'edit' && (
        <EuiFormRow label={getLegacyUserFieldTranslation('id')}>
          <EuiFieldText readOnly value={values.enrollment} />
        </EuiFormRow>
      )}

      <EuiFormRow
        error={getFieldErrorMessage(fieldErrors.type)}
        isInvalid={Boolean(fieldErrors.type)}
        label={getLegacyUserFieldTranslation('type')}
      >
        <EuiSelect
          isInvalid={Boolean(fieldErrors.type)}
          options={userTypes.map((userType) => ({
            value: String(userType.id),
            text: userType.name,
          }))}
          value={values.type == null ? '' : String(values.type)}
          onChange={(event) =>
            onChange({
              ...values,
              type: event.target.value ? Number(event.target.value) : null,
            })
          }
        />
      </EuiFormRow>

      <EuiFormRow
        error={getFieldErrorMessage(fieldErrors.status)}
        isInvalid={Boolean(fieldErrors.status)}
        label={getLegacyUserFieldTranslation('status')}
      >
        {/* TODO: fix this when we have a proper type for the options */}
        <TypedEuiSelect
          isInvalid={Boolean(fieldErrors.status)}
          options={USER_STATUS_OPTIONS.map((option) => ({
            value: option.value,
            text: getLegacyTranslation(option.labelKey as any),
          }))}
          value={values.status}
          onChange={(event) =>
            onChange({
              ...values,
              status: event.target.value as CirculationUserFormValues['status'],
            })
          }
        />
      </EuiFormRow>

      <EuiFormRow label={getLegacyUserFieldTranslation('photo')}>
        <EuiFlexPhotoField
          isPhotoLoading={isPhotoLoading}
          photoPreviewUrl={photoPreviewUrl}
          onPhotoSelect={onPhotoSelect}
        />
      </EuiFormRow>

      {fields.map((field) => (
        <DynamicUserField
          key={field.key}
          error={getFieldErrorMessage(fieldErrors[field.key])}
          field={field}
          isInvalid={Boolean(fieldErrors[field.key])}
          value={values.fields[field.key] ?? ''}
          onChange={(value) => updateFieldValue(field.key, value)}
        />
      ))}
    </EuiForm>
  )
}

type DynamicUserFieldProps = {
  field: UserSearchableField
  value: string
  error?: string
  isInvalid: boolean
  onChange: (value: string) => void
}

const DynamicUserField: FC<DynamicUserFieldProps> = ({
  field,
  value,
  error,
  isInvalid,
  onChange,
}) => {
  const label = getLegacyUserFieldTranslation(field.key)

  switch (field.type) {
    case 'text':
      return (
        <EuiFormRow error={error} isInvalid={isInvalid} label={label}>
          <EuiTextArea
            isInvalid={isInvalid}
            value={value}
            onChange={(event) => onChange(event.target.value)}
          />
        </EuiFormRow>
      )
    case 'list':
      return (
        <EuiFormRow error={error} isInvalid={isInvalid} label={label}>
          <EuiSelect
            hasNoInitialSelection={!field.required}
            isInvalid={isInvalid}
            options={getListFieldOptions(field.key, field.maxLength)}
            value={value}
            onChange={(event) => onChange(event.target.value)}
          />
        </EuiFormRow>
      )
    case 'date':
    case 'datetime':
      return (
        <EuiFormRow error={error} isInvalid={isInvalid} label={label}>
          <EuiDatePicker
            dateFormat={DAY_FIRST_DATE_FORMAT}
            locale='pt-br'
            selected={value ? moment(value, DAY_FIRST_DATE_FORMAT) : null}
            onChange={(date) =>
              onChange(date ? date.format(DAY_FIRST_DATE_FORMAT) : '')
            }
          />
        </EuiFormRow>
      )
    case 'boolean':
      return (
        <EuiFormRow error={error} isInvalid={isInvalid} label={label}>
          <EuiSwitch
            checked={value === 'true'}
            label=''
            onChange={(event) => onChange(event.target.checked ? 'true' : '')}
          />
        </EuiFormRow>
      )
    case 'number':
      return (
        <EuiFormRow error={error} isInvalid={isInvalid} label={label}>
          <EuiFieldNumber
            isInvalid={isInvalid}
            value={value}
            onChange={(event) => onChange(event.target.value)}
          />
        </EuiFormRow>
      )
    default:
      return (
        <EuiFormRow error={error} isInvalid={isInvalid} label={label}>
          <EuiFieldText
            isInvalid={isInvalid}
            maxLength={field.maxLength > 0 ? field.maxLength : undefined}
            value={value}
            onChange={(event) => onChange(event.target.value)}
          />
        </EuiFormRow>
      )
  }
}

type PhotoFieldProps = {
  photoPreviewUrl?: string
  isPhotoLoading: boolean
  onPhotoSelect: (file: File | null) => void
}

const EuiFlexPhotoField: FC<PhotoFieldProps> = ({
  photoPreviewUrl,
  isPhotoLoading,
  onPhotoSelect,
}) => {
  return (
    <Fragment>
      {photoPreviewUrl ? (
        <EuiAvatar
          css={{ marginBottom: '0.75rem' }}
          imageUrl={photoPreviewUrl}
          name=''
          size='xl'
          type='space'
        />
      ) : null}
      <EuiFilePicker
        accept='.jpg,.jpeg,.png,.gif'
        display='default'
        isLoading={isPhotoLoading}
        onChange={(files) => onPhotoSelect(files?.[0] ?? null)}
      />
    </Fragment>
  )
}

export default CirculationUserForm
