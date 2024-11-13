import type { FC } from "react";
import { useState } from "react";
import React from "react";
import {
  EuiButton,
  EuiButtonEmpty,
  EuiFieldText,
  EuiFlexGroup,
  EuiFlexItem,
  EuiForm,
  EuiFormRow,
  EuiModal,
  EuiModalBody,
  EuiModalFooter,
  EuiModalHeader,
  EuiModalHeaderTitle,
  EuiSelect,
  EuiSwitch,
  useGeneratedHtmlId,
} from "@elastic/eui";
import { FormattedMessage } from "react-intl";
import type { SubfieldCode } from "../types";
import { ALPHANUMERIC_CHARACTERS_VALUES } from "./types";
import {
  AUTOCOMPLETE_VALUES,
  toAutoCompleteType,
  toSubfieldCode,
} from "../lib";
import type { SubfieldFormEditorState } from "../queries";
import type { EuiSelectOption } from "@elastic/eui/src/components/form/select/select";

type SubfieldEditModalProps = {
  subfieldFormEditorState: SubfieldFormEditorState;
  disabledCodes: Set<SubfieldCode>;
  onCloseModal: () => void;
  onConfirm: (subfield: SubfieldFormEditorState) => void;
};

const SubfieldEditModal: FC<SubfieldEditModalProps> = ({
  subfieldFormEditorState,
  disabledCodes,
  onCloseModal,
  onConfirm,
}) => {
  const [editedSubfield, setEditedSubfield] = useState(subfieldFormEditorState);

  const modalTitleId = useGeneratedHtmlId();

  const modalFormId = useGeneratedHtmlId({ prefix: "modalForm" });

  const autoCompleteOptions = AUTOCOMPLETE_VALUES.map((value) => ({
    value,
    text: value,
  }));

  const options: EuiSelectOption[] = ALPHANUMERIC_CHARACTERS_VALUES.map(
    (value) => ({
      disabled: disabledCodes.has(value),
      text: value,
    }),
  );

  return (
    <EuiModal
      aria-labelledby={modalTitleId}
      onClose={onCloseModal}
      initialFocus="[name=popswitch]"
    >
      <EuiModalHeader>
        <EuiModalHeaderTitle id={modalTitleId}>
          <FormattedMessage
            id="administration.customization.subfield.edit.modal.title"
            defaultMessage="Editar valor do subcampo"
          />
        </EuiModalHeaderTitle>
      </EuiModalHeader>

      <EuiModalBody>
        <EuiForm>
          <EuiFlexGroup direction="column">
            <EuiFlexGroup>
              <EuiFlexItem grow={false}>
                <EuiFormRow
                  label={
                    <FormattedMessage
                      id="administration.customization.subfield.code"
                      defaultMessage="Código"
                    />
                  }
                >
                  <EuiSelect
                    options={options}
                    value={editedSubfield.code}
                    onChange={(event) => {
                      setEditedSubfield({
                        ...editedSubfield,
                        code: toSubfieldCode(event.target.value),
                      });
                    }}
                  />
                </EuiFormRow>
              </EuiFlexItem>
              <EuiFlexItem>
                <EuiFormRow
                  label={
                    <FormattedMessage
                      id="administration.customization.subfield.description"
                      defaultMessage="Descrição"
                    />
                  }
                >
                  <EuiFieldText
                    name="description"
                    value={subfieldFormEditorState.description}
                    onChange={(event) =>
                      setEditedSubfield({
                        ...subfieldFormEditorState,
                        description: event.target.value,
                      })
                    }
                  ></EuiFieldText>
                </EuiFormRow>
              </EuiFlexItem>
            </EuiFlexGroup>
            <EuiFlexGroup>
              <EuiFlexItem grow={false}>
                <EuiFormRow
                  label={
                    <FormattedMessage
                      id="administration.customization.subfield.repeatable"
                      defaultMessage="Repetível"
                    />
                  }
                >
                  <EuiSwitch
                    label={
                      <FormattedMessage
                        id="administration.customization.subfield.repeatable"
                        defaultMessage="Repetível"
                      />
                    }
                    onChange={() => {
                      setEditedSubfield({
                        ...editedSubfield,
                        repeatable: !editedSubfield.repeatable,
                      });
                    }}
                    checked={editedSubfield.repeatable}
                  />
                </EuiFormRow>
              </EuiFlexItem>
              <EuiFlexItem grow={false}>
                <EuiFormRow
                  label={
                    <FormattedMessage
                      id="administration.customization.subfield.collapsed"
                      defaultMessage="Recolhido"
                    />
                  }
                >
                  <EuiSwitch
                    label={
                      <FormattedMessage
                        id="administration.customization.subfield.collapsed"
                        defaultMessage="Recolhido"
                      />
                    }
                    onChange={() => {
                      setEditedSubfield({
                        ...editedSubfield,
                        collapsed: !editedSubfield.collapsed,
                      });
                    }}
                    checked={editedSubfield.collapsed}
                  />
                </EuiFormRow>
              </EuiFlexItem>
              <EuiFlexItem grow={false}>
                <EuiFormRow
                  label={
                    <FormattedMessage
                      id="administration.customization.subfield.autocomplete_type"
                      defaultMessage="Tipo de auto-completar"
                    />
                  }
                >
                  <EuiSelect
                    options={autoCompleteOptions}
                    value={editedSubfield.autocompleteType}
                    onChange={(event) => {
                      setEditedSubfield({
                        ...editedSubfield,
                        autocompleteType: toAutoCompleteType(
                          event.target.value,
                        ),
                      });
                    }}
                  />
                </EuiFormRow>
              </EuiFlexItem>
            </EuiFlexGroup>
          </EuiFlexGroup>
        </EuiForm>
      </EuiModalBody>

      <EuiModalFooter>
        <EuiButtonEmpty onClick={onCloseModal}>Cancel</EuiButtonEmpty>

        <EuiButton
          type="submit"
          form={modalFormId}
          onClick={() => {
            onConfirm(editedSubfield);

            onCloseModal();
          }}
          fill
        >
          Save
        </EuiButton>
      </EuiModalFooter>
    </EuiModal>
  );
};

export default SubfieldEditModal;
