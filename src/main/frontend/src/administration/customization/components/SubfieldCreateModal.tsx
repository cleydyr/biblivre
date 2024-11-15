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
import { FormattedMessage, useIntl } from "react-intl";
import type { SubfieldCode } from "../types";
import type { SubfieldFormEditorState } from "./types";
import { ALPHANUMERIC_CHARACTERS_VALUES } from "./types";
import {
  AUTOCOMPLETE_VALUES,
  toAutoCompleteType,
  toSubfieldCode,
} from "../lib";
import type { EuiSelectOption } from "@elastic/eui/src/components/form/select/select";
import { autocompleteTypeMessageDescriptors } from "../messages";

type SubfieldCreateModalProps = {
  disabledCodes: Set<SubfieldCode>;
  onCloseModal: () => void;
  onConfirm: (subfield: SubfieldFormEditorState) => void;
};

const SubfieldCreateModal: FC<SubfieldCreateModalProps> = ({
  disabledCodes,
  onCloseModal,
  onConfirm,
}) => {
  const { formatMessage } = useIntl();

  const modalTitleId = useGeneratedHtmlId();

  const modalFormId = useGeneratedHtmlId({ prefix: "modalForm" });

  const autoCompleteOptions = AUTOCOMPLETE_VALUES.map((value) => ({
    value,
    text: formatMessage(autocompleteTypeMessageDescriptors[value]),
  }));

  const options: EuiSelectOption[] = ALPHANUMERIC_CHARACTERS_VALUES.map(
    (value) => ({
      disabled: disabledCodes.has(value),
      text: value,
    }),
  );

  const firstEnabledCode = ALPHANUMERIC_CHARACTERS_VALUES.find(
    (value) => !disabledCodes.has(value),
  );

  if (firstEnabledCode === undefined) {
    throw new Error("All subfield codes are disabled");
  }

  const [editedSubfield, setEditedSubfield] = useState<SubfieldFormEditorState>(
    {
      code: firstEnabledCode,
      description: "",
      repeatable: false,
      collapsed: false,
      autocompleteType: "disabled",
      sortOrder: 0,
    },
  );

  const { code, description, collapsed, repeatable, autocompleteType } =
    editedSubfield;

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
                    value={code}
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
                  error={[
                    <FormattedMessage
                      key={0}
                      id="administration.customization.subfield.description.error"
                      defaultMessage="A descrição é obrigatória"
                    />,
                  ]}
                  isInvalid={description === ""}
                >
                  <EuiFieldText
                    name="description"
                    value={description}
                    onChange={(event) =>
                      setEditedSubfield({
                        ...editedSubfield,
                        description: event.target.value,
                      })
                    }
                    minLength={1}
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
                        repeatable: !repeatable,
                      });
                    }}
                    checked={repeatable}
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
                        collapsed: !collapsed,
                      });
                    }}
                    checked={collapsed}
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
                    value={autocompleteType}
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
        <EuiButtonEmpty onClick={onCloseModal}>
          <FormattedMessage
            id="administration.customization.subfield.edit.modal.cancel"
            defaultMessage="Cancelar"
          />
        </EuiButtonEmpty>

        <EuiButton
          type="submit"
          form={modalFormId}
          onClick={() => {
            onConfirm(editedSubfield);

            onCloseModal();
          }}
          fill
        >
          <FormattedMessage
            id="administration.customization.subfield.edit.modal.confirm"
            defaultMessage="Confirmar"
          />
        </EuiButton>
      </EuiModalFooter>
    </EuiModal>
  );
};

export default SubfieldCreateModal;
