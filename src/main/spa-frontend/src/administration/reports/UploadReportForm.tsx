import {
  EuiButton,
  EuiDescribedFormGroup,
  EuiFieldText,
  EuiFilePicker,
  EuiForm,
  EuiFormRow,
  EuiSpacer,
  EuiText,
  EuiTextArea,
  EuiTitle,
  useGeneratedHtmlId,
} from '@elastic/eui'
import { useRef, useState } from 'react'

export type UploadReportFormData = {
  file: File
  title: string
  description: string
}

type UploadReportFormProps = {
  onSubmit: (form: UploadReportFormData) => void
}

const UploadReportForm = ({ onSubmit }: UploadReportFormProps) => {
  const [fileToUpload, setFileToUpload] = useState(
    undefined as File | undefined,
  )

  const [title, setTitle] = useState('')

  const [description, setDescription] = useState('')

  const filePickerRef = useRef(EuiFilePicker.prototype)

  const removeFilePickerId = useGeneratedHtmlId({ prefix: 'removeFilePicker' })

  return (
    <EuiForm>
      <EuiFormRow label='Título'>
        <EuiFieldText
          aria-label='Título do relatório'
          name='title'
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
      </EuiFormRow>
      <EuiFormRow label='Descrição'>
        <EuiTextArea
          aria-label='Descrição do relatório'
          name='description'
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </EuiFormRow>
      <EuiFormRow>
        <EuiDescribedFormGroup
          description='Faça upload de um relatório personalizado no formato .jrxml'
          title={
            <EuiTitle size='m'>
              <EuiText>Upload de relatório</EuiText>
            </EuiTitle>
          }
        >
          <EuiFilePicker
            ref={filePickerRef}
            accept='.jrxml'
            aria-label='Selecionador de arquivo para upload de relatório'
            id={removeFilePickerId}
            initialPromptText='Arraste e solte ou clique para fazer upload'
            multiple={false}
            onChange={(files) => {
              if (files === null) return

              setFileToUpload(files[0])
            }}
          />
          <EuiSpacer />
          {fileToUpload && (
            <EuiButton
              fill
              onClick={() => {
                onSubmit({
                  file: fileToUpload,
                  title,
                  description,
                })

                filePickerRef.current.removeFiles()
              }}
            >
              Enviar
            </EuiButton>
          )}
        </EuiDescribedFormGroup>
      </EuiFormRow>
    </EuiForm>
  )
}

export default UploadReportForm
