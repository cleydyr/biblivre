import type { FormData } from "../../generated-sources";
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
import { useTranslationsQuery } from "./queries";
import { useSaveFormDataFieldsMutation2 } from "./queries";
import { toFormFieldEditorState } from "./components/lib";
import type { FormFieldEditorState } from "./components/types";

type EditDataFieldFlyoutProps = {
  datafield: FormData;
  onClose: () => void;
  onSave: () => void;
};

const EditDatafieldFlyout: FC<EditDataFieldFlyoutProps> = ({
  datafield,
  onClose,
}) => {
  const { mutate: saveFormDataFieldsMtn } = useSaveFormDataFieldsMutation2();

  const simpleFlyoutTitleId = useGeneratedHtmlId({
    prefix: "simpleFlyoutTitle",
  });

  const { data: translations, isSuccess } = useTranslationsQuery("pt-BR");

  const editorState = isSuccess
    ? toFormFieldEditorState(translations, datafield)
    : undefined;

  const [formFieldEditorState, setFormFieldEditorState] = useState<
    FormFieldEditorState | undefined
  >(editorState);

  if (!isSuccess || formFieldEditorState === undefined) {
    return null;
  }

  return (
    <EuiFlyout ownFocus onClose={onClose} aria-labelledby={simpleFlyoutTitleId}>
      <EuiFlyoutHeader>
        <EuiTitle size="m">
          <h2 id={simpleFlyoutTitleId}>
            <FormattedMessage
              id="administration.customization.datafield.edit"
              defaultMessage="Editar campo {tag}"
              values={{
                tag: datafield.datafield,
              }}
            />
          </h2>
        </EuiTitle>
      </EuiFlyoutHeader>
      <EuiFlyoutBody>
        <FormFieldEditor
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
            <EuiButton
              fill
              onClick={() => {
                if (formFieldEditorState) {
                  saveFormDataFieldsMtn({
                    formFieldEditorState,
                    recordType: "biblio",
                  });

                  onClose();
                }
              }}
            >
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
