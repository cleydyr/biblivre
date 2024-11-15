import type { FC } from "react";
import { useState } from "react";
import React from "react";
import {
  EuiButton,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFlyout,
  EuiFlyoutBody,
  EuiFlyoutFooter,
  EuiFlyoutHeader,
  EuiTitle,
  useGeneratedHtmlId,
} from "@elastic/eui";
import { FormattedMessage } from "react-intl";
import FormFieldEditor from "./FormFieldEditor";
import type { FormFieldEditorState } from "./components/types";

type EditDataFieldFlyoutProps = {
  editorState: FormFieldEditorState;
  mode: "edit" | "create";
  onClose: () => void;
  onSave: (formFieldEditorState: FormFieldEditorState) => void;
};

const EditDatafieldFlyout: FC<EditDataFieldFlyoutProps> = ({
  editorState,
  onClose,
  onSave,
  mode,
}) => {
  const simpleFlyoutTitleId = useGeneratedHtmlId({
    prefix: "simpleFlyoutTitle",
  });

  const [formFieldEditorState, setFormFieldEditorState] =
    useState<FormFieldEditorState>(editorState);

  return (
    <EuiFlyout ownFocus onClose={onClose} aria-labelledby={simpleFlyoutTitleId}>
      <EuiFlyoutHeader>
        <EuiTitle size="m">
          <h2 id={simpleFlyoutTitleId}>
            <FormattedMessage
              id="administration.customization.datafield.edit"
              defaultMessage="Editar campo {tag}"
              values={{
                tag: formFieldEditorState.tag,
              }}
            />
          </h2>
        </EuiTitle>
      </EuiFlyoutHeader>
      <EuiFlyoutBody>
        <FormFieldEditor
          mode={mode}
          editorState={formFieldEditorState}
          onChange={setFormFieldEditorState}
        />
      </EuiFlyoutBody>
      <EuiFlyoutFooter>
        <EuiFlexGroup justifyContent="flexEnd">
          <EuiFlexItem grow={false}>
            <EuiButton onClick={onClose}>
              <FormattedMessage
                id="administration.customization.form.cancel"
                defaultMessage="Cancelar"
              />
            </EuiButton>
          </EuiFlexItem>
          <EuiFlexItem grow={false}>
            <EuiButton fill onClick={() => onSave(formFieldEditorState)}>
              <FormattedMessage
                id="administration.customization.form.save"
                defaultMessage="Salvar"
              />
            </EuiButton>
          </EuiFlexItem>
        </EuiFlexGroup>
      </EuiFlyoutFooter>
    </EuiFlyout>
  );
};

export default EditDatafieldFlyout;
