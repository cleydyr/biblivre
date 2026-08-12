import {
  EuiButton,
  EuiButtonEmpty,
  EuiCallOut,
  EuiConfirmModal,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFlyout,
  EuiFlyoutBody,
  EuiFlyoutFooter,
  EuiFlyoutHeader,
  EuiSpacer,
  EuiTitle,
  useGeneratedHtmlId,
} from '@elastic/eui'
import { Fragment, useEffect, useMemo, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import {
  useAddressLookupEnabled,
  useLookupAddressByCepMutation,
} from '../api-helpers/address-lookup/hooks'
import { useSaveCirculationUserMutation } from '../api-helpers/circulation/hooks'
import { useUserFields } from '../api-helpers/user-fields/hooks'
import { useUserTypes } from '../api-helpers/user-type/hooks'
import LoadingState from '../components/LoadingState'
import { ResponseError } from '../generated-sources'
import { getLegacyTranslation } from '../legacy_translations/lib'
import { useToasts } from '../toasts/useToasts'

import {
  addressLookupToFieldValues,
  applyAddressLookupValues,
  hasAddressFieldsToOverwrite,
  isValidBrazilianCep,
} from './addressLookupLogic'
import CirculationUserForm from './CirculationUserForm'
import {
  areFormValuesDirty,
  buildSavePayload,
  convertImageFileToJpegBase64,
  createEmptyUserFormValues,
  isAcceptedPhotoType,
  parseServerFieldErrors,
  userToFormValues,
  validateUserFormValues,
} from './circulationUserFormLogic'

import type { FC } from 'react'

import type { User } from '../api-helpers/circulation/response-types'
import type { AddressLookupResult } from '../generated-sources'

import type { CirculationUserFormValues } from './circulationUserFormLogic'

type Props = {
  mode: 'create' | 'edit'
  user?: User
  onClose: () => void
  onSaved: (user: User) => void
}

const CirculationUserFormFlyout: FC<Props> = ({
  mode,
  user,
  onClose,
  onSaved,
}) => {
  const flyoutTitleId = useGeneratedHtmlId()
  const { showToast } = useToasts()

  const { data: userFields = [], isLoading: isFieldsLoading } = useUserFields()
  const { data: userTypes = [], isLoading: isTypesLoading } = useUserTypes()
  const { data: addressLookupEnabled = false } = useAddressLookupEnabled()
  const { mutate: lookupAddressByCep, isPending: isAddressLookupLoading } =
    useLookupAddressByCepMutation()

  const { mutate: saveUser, isPending: isSaving } =
    useSaveCirculationUserMutation()

  const defaultTypeId = userTypes[0]?.id ?? null

  const [values, setValues] = useState<CirculationUserFormValues>(() =>
    mode === 'edit' && user
      ? userToFormValues(user)
      : createEmptyUserFormValues(defaultTypeId),
  )
  const [initialValues, setInitialValues] = useState(values)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const [saveErrorMessage, setSaveErrorMessage] = useState<string | null>(null)
  const [isCancelConfirmOpen, setIsCancelConfirmOpen] = useState(false)
  const [isPhotoLoading, setIsPhotoLoading] = useState(false)
  const [pendingAddressLookup, setPendingAddressLookup] =
    useState<AddressLookupResult | null>(null)
  const overwriteConfirmTitleId = useGeneratedHtmlId()

  useEffect(() => {
    if (mode === 'create' && values.type == null) {
      const nextValues = createEmptyUserFormValues(defaultTypeId)
      setValues(nextValues)
      setInitialValues(nextValues)
    }
  }, [defaultTypeId, mode, values.type])

  useEffect(() => {
    if (mode === 'edit' && user) {
      const nextValues = userToFormValues(user)
      setValues(nextValues)
      setInitialValues(nextValues)
    }
  }, [mode, user])

  const isLoading = isFieldsLoading || isTypesLoading

  const handleCloseRequest = () => {
    if (areFormValuesDirty(values, initialValues)) {
      setIsCancelConfirmOpen(true)
      return
    }

    onClose()
  }

  const handleSave = (saveAsNew: boolean) => {
    const validationErrors = validateUserFormValues(values, userFields)

    if (Object.keys(validationErrors).length > 0) {
      setFieldErrors(validationErrors)
      return
    }

    setFieldErrors({})
    setSaveErrorMessage(null)

    saveUser(buildSavePayload(values, saveAsNew), {
      onSuccess: (response) => {
        if (!response.success) {
          setFieldErrors(parseServerFieldErrors(response.errors))
          setSaveErrorMessage(response.message)
          return
        }

        showToast({
          id: `circulation-user-save-${Date.now()}`,
          title: response.message ?? '',
          color: 'success',
          iconType: 'check',
        })
        onSaved(response.data)
        onClose()
      },
    })
  }

  const applyLookupResult = (result: AddressLookupResult) => {
    const lookupValues = addressLookupToFieldValues(result)
    setValues((currentValues) => ({
      ...currentValues,
      fields: applyAddressLookupValues(currentValues.fields, lookupValues),
    }))

    if (result.incomplete) {
      showToast({
        id: `circulation-user-address-incomplete-${Date.now()}`,
        title: getLegacyTranslation(
          'circulation.user.address_lookup.incomplete',
        ),
        color: 'warning',
        iconType: 'alert',
      })
    }
  }

  const handleAddressLookup = () => {
    const cep = values.fields.address_zip || ''

    if (!isValidBrazilianCep(cep)) {
      showToast({
        id: `circulation-user-address-invalid-${Date.now()}`,
        title: getLegacyTranslation(
          'circulation.user.address_lookup.invalid_cep',
        ),
        color: 'warning',
        iconType: 'alert',
      })
      return
    }

    lookupAddressByCep(cep, {
      onSuccess: (result) => {
        const lookupValues = addressLookupToFieldValues(result)
        if (hasAddressFieldsToOverwrite(values.fields, lookupValues)) {
          setPendingAddressLookup(result)
          return
        }

        applyLookupResult(result)
      },
      onError: (error) => {
        let title = getLegacyTranslation(
          'circulation.user.address_lookup.error',
        )

        if (error instanceof ResponseError) {
          if (error.response.status === 404) {
            title = getLegacyTranslation(
              'circulation.user.address_lookup.not_found',
            )
          } else if (error.response.status === 400) {
            title = getLegacyTranslation(
              'circulation.user.address_lookup.invalid_cep',
            )
          }
        }

        showToast({
          id: `circulation-user-address-error-${Date.now()}`,
          title,
          color: 'danger',
          iconType: 'alert',
        })
      },
    })
  }

  const handlePhotoSelect = async (file: File | null) => {
    if (!file) {
      setValues((currentValues) => ({
        ...currentValues,
        photoData: null,
        photoPreviewUrl: null,
      }))
      return
    }

    if (!isAcceptedPhotoType(file)) {
      showToast({
        id: `circulation-user-photo-error-${Date.now()}`,
        title: 'Extensão de foto inválida',
        color: 'warning',
        iconType: 'alert',
      })
      return
    }

    setIsPhotoLoading(true)

    try {
      const { base64, previewUrl } = await convertImageFileToJpegBase64(file)
      setValues((currentValues) => ({
        ...currentValues,
        photoData: base64,
        photoPreviewUrl: previewUrl,
      }))
    } finally {
      setIsPhotoLoading(false)
    }
  }

  const title = useMemo(() => {
    if (mode === 'create') {
      return (
        <FormattedMessage
          defaultMessage='Novo usuário'
          id='circulation.user.button.new'
        />
      )
    }

    return <FormattedMessage defaultMessage='Editar usuário' id='common.edit' />
  }, [mode])

  return (
    <Fragment>
      <EuiFlyout
        ownFocus
        aria-labelledby={flyoutTitleId}
        onClose={handleCloseRequest}
      >
        <EuiFlyoutHeader hasBorder>
          <EuiTitle size='m'>
            <h2 id={flyoutTitleId}>{title}</h2>
          </EuiTitle>
        </EuiFlyoutHeader>
        <EuiFlyoutBody>
          {isLoading ? (
            <LoadingState />
          ) : (
            <Fragment>
              {saveErrorMessage ? (
                <Fragment>
                  <EuiCallOut
                    announceOnMount
                    color='danger'
                    title={saveErrorMessage}
                  />
                  <EuiSpacer />
                </Fragment>
              ) : null}
              <CirculationUserForm
                addressLookupEnabled={addressLookupEnabled}
                existingPhotoId={user?.photo_id}
                fieldErrors={fieldErrors}
                fields={userFields}
                isAddressLookupLoading={isAddressLookupLoading}
                isPhotoLoading={isPhotoLoading}
                mode={mode}
                userTypes={userTypes}
                values={values}
                onAddressLookup={handleAddressLookup}
                onChange={setValues}
                onPhotoSelect={handlePhotoSelect}
              />
            </Fragment>
          )}
        </EuiFlyoutBody>
        {!isLoading ? (
          <EuiFlyoutFooter>
            <EuiFlexGroup gutterSize='s' justifyContent='flexEnd'>
              <EuiFlexItem grow={false}>
                <EuiButtonEmpty onClick={handleCloseRequest}>
                  <FormattedMessage
                    defaultMessage='Cancelar'
                    id='common.cancel'
                  />
                </EuiButtonEmpty>
              </EuiFlexItem>
              {mode === 'edit' ? (
                <EuiFlexItem grow={false}>
                  <EuiButton
                    isLoading={isSaving}
                    onClick={() => handleSave(true)}
                  >
                    <FormattedMessage
                      defaultMessage='Salvar como Novo'
                      id='common.save_as_new'
                    />
                  </EuiButton>
                </EuiFlexItem>
              ) : null}
              <EuiFlexItem grow={false}>
                <EuiButton
                  fill
                  isLoading={isSaving}
                  onClick={() => handleSave(false)}
                >
                  <FormattedMessage defaultMessage='Salvar' id='common.save' />
                </EuiButton>
              </EuiFlexItem>
            </EuiFlexGroup>
          </EuiFlyoutFooter>
        ) : null}
      </EuiFlyout>
      {isCancelConfirmOpen ? (
        <EuiConfirmModal
          cancelButtonText={
            <FormattedMessage defaultMessage='Não' id='common.no' />
          }
          confirmButtonText={
            <FormattedMessage defaultMessage='Sim' id='common.yes' />
          }
          title={
            <FormattedMessage
              defaultMessage='Cancelar edição de usuáro'
              id='circulation.user.confirm_cancel_editing_title'
            />
          }
          onCancel={() => setIsCancelConfirmOpen(false)}
          onConfirm={() => {
            setIsCancelConfirmOpen(false)
            onClose()
          }}
        >
          <p>
            <FormattedMessage
              defaultMessage='Você deseja cancelar a edição deste usuário?'
              id='circulation.user.confirm_cancel_editing.1'
            />
          </p>
          <p>
            <FormattedMessage
              defaultMessage='Todas as alterações serão perdidas'
              id='circulation.user.confirm_cancel_editing.2'
            />
          </p>
        </EuiConfirmModal>
      ) : null}
      {pendingAddressLookup ? (
        <EuiConfirmModal
          aria-labelledby={overwriteConfirmTitleId}
          cancelButtonText={
            <FormattedMessage defaultMessage='Não' id='common.no' />
          }
          confirmButtonText={
            <FormattedMessage defaultMessage='Sim' id='common.yes' />
          }
          title={getLegacyTranslation(
            'circulation.user.address_lookup.confirm_overwrite_title',
          )}
          titleProps={{ id: overwriteConfirmTitleId }}
          onCancel={() => setPendingAddressLookup(null)}
          onConfirm={() => {
            applyLookupResult(pendingAddressLookup)
            setPendingAddressLookup(null)
          }}
        >
          <p>
            {getLegacyTranslation(
              'circulation.user.address_lookup.confirm_overwrite_message',
            )}
          </p>
        </EuiConfirmModal>
      ) : null}
    </Fragment>
  )
}

export default CirculationUserFormFlyout
