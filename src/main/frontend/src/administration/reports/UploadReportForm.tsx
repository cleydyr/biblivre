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
} from "@elastic/eui";
import React, { useRef, useState } from "react";

export type UploadReportFormData = {
  file: File;
  title: string;
  description: string;
};

type UploadReportFormProps = {
  onSubmit: (form: UploadReportFormData) => void;
};

const UploadReportForm = ({ onSubmit }: UploadReportFormProps) => {
  const [fileToUpload, setFileToUpload] = useState(
    undefined as File | undefined
  );

  const [title, setTitle] = useState("");

  const [description, setDescription] = useState("");

  const filePickerRef = useRef(EuiFilePicker.prototype);

  const removeFilePickerId = useGeneratedHtmlId({ prefix: "removeFilePicker" });

  return (
    <EuiForm>
      <EuiFormRow label="Título">
        <EuiFieldText
          name="title"
          aria-label="Título do relatório"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
      </EuiFormRow>
      <EuiFormRow label="Descrição">
        <EuiTextArea
          name="description"
          aria-label="Descrição do relatório"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </EuiFormRow>
      <EuiFormRow>
        <EuiDescribedFormGroup
          title={
            <EuiTitle size="m">
              <EuiText>Upload de relatório</EuiText>
            </EuiTitle>
          }
          description="Faça upload de um relatório personalizado no formato .jrxml"
        >
          <EuiFilePicker
            ref={filePickerRef}
            initialPromptText="Arraste e solte ou clique para fazer upload"
            onChange={(files) => {
              if (files === null) return;

              setFileToUpload(files[0]);
            }}
            id={removeFilePickerId}
            accept=".jrxml"
            aria-label="Selecionador de arquivo para upload de relatório"
            multiple={false}
          />
          <EuiSpacer />
          {fileToUpload && (
            <EuiButton
              onClick={() => {
                onSubmit({
                  file: fileToUpload,
                  title,
                  description,
                });

                filePickerRef.current.removeFiles();
              }}
              fill
            >
              Enviar
            </EuiButton>
          )}
        </EuiDescribedFormGroup>
      </EuiFormRow>
    </EuiForm>
  );
};

export default UploadReportForm;
