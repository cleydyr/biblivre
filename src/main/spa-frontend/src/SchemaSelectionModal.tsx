import {
  EuiButton,
  EuiButtonEmpty,
  EuiModal,
  EuiModalBody,
  EuiModalFooter,
  EuiModalHeader,
  EuiModalHeaderTitle,
  EuiRadioGroup,
  EuiSpacer,
} from '@elastic/eui'
import { useEffect, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import type { FC } from 'react'

import type { SchemaListItem } from './api-helpers/types'

type Props = {
  isOpen: boolean
  schemas: SchemaListItem[]
  initialSchemaId: string | null
  onClose: () => void
  onConfirm: (schemaId: string) => void
}

const SchemaSelectionModal: FC<Props> = ({
  isOpen,
  schemas,
  initialSchemaId,
  onClose,
  onConfirm,
}) => {
  const [selectedId, setSelectedId] = useState(
    () => initialSchemaId ?? schemas[0]?.schema ?? '',
  )

  useEffect(() => {
    if (isOpen) {
      setSelectedId(initialSchemaId ?? schemas[0]?.schema ?? '')
    }
  }, [initialSchemaId, isOpen, schemas])

  if (!isOpen) {
    return null
  }

  return (
    <EuiModal onClose={onClose}>
      <EuiModalHeader>
        <EuiModalHeaderTitle>
          <FormattedMessage
            defaultMessage='Selecionar biblioteca'
            id='app.schema.modal.title'
          />
        </EuiModalHeaderTitle>
      </EuiModalHeader>
      <EuiModalBody>
        <EuiRadioGroup
          compressed
          idSelected={selectedId}
          name='biblivre-schema'
          options={schemas.map((s) => ({
            id: s.schema,
            label: s.name || s.schema,
          }))}
          onChange={(id) => setSelectedId(id)}
        />
        <EuiSpacer size='m' />
      </EuiModalBody>
      <EuiModalFooter>
        <EuiButtonEmpty onClick={onClose}>
          <FormattedMessage
            defaultMessage='Cancelar'
            id='app.schema.modal.cancel'
          />
        </EuiButtonEmpty>
        <EuiButton
          fill
          disabled={!selectedId}
          onClick={() => onConfirm(selectedId)}
        >
          <FormattedMessage
            defaultMessage='Confirmar'
            id='app.schema.modal.confirm'
          />
        </EuiButton>
      </EuiModalFooter>
    </EuiModal>
  )
}

export default SchemaSelectionModal
