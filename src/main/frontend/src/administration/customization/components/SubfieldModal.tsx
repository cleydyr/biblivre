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
import type { SubfieldFormEditorState } from "./types";
import { ALPHANUMERIC_CHARACTERS_VALUES } from "./types";
import {
  AUTOCOMPLETE_VALUES,
  toAutoCompleteType,
  toSubfieldCode,
} from "../lib";
import { autocompleteTypeMessageDescriptors } from "../messages";

type SubfieldEditModalBaseProps = {
  title: string;
  subfieldFormEditorState: SubfieldFormEditorState;
  onCloseModal: () => void;
  onConfirm: (subfield: SubfieldFormEditorState) => void;
  mode: "edit" | "create";
};

type SubfieldEditModalProps = SubfieldEditModalBaseProps & {
  mode: "edit";
};

type SubfieldCreateModalProps = SubfieldEditModalBaseProps & {
  mode: "create";
  disabledCodes: Set<string>;
};

type SubfieldModalProps = SubfieldEditModalProps | SubfieldCreateModalProps;

const SubfieldModal: FC<SubfieldModalProps> = (props) => {
  const { title, subfieldFormEditorState, onCloseModal, onConfirm, mode } =
    props;

  const { formatMessage } = useIntl();

  const [editedSubfield, setEditedSubfield] = useState(subfieldFormEditorState);

  const modalTitleId = useGeneratedHtmlId();

  const modalFormId = useGeneratedHtmlId({ prefix: "modalForm" });

  const autoCompleteOptions = AUTOCOMPLETE_VALUES.map((value) => ({
    value,
    text: formatMessage(autocompleteTypeMessageDescriptors[value]),
  }));

  const { code, description, collapsed, repeatable, autocompleteType } =
    editedSubfield;

  return (
    <EuiModal
      aria-labelledby={modalTitleId}
      onClose={onCloseModal}
      initialFocus="[name=popswitch]"
    >
      <EuiModalHeader>
        <EuiModalHeaderTitle id={modalTitleId}>{title}</EuiModalHeaderTitle>
      </EuiModalHeader>

      <EuiModalBody>
        <EuiForm>
          <EuiFlexGroup direction="column">
            <EuiFlexGroup>
              {mode === "create" && (
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
                      options={ALPHANUMERIC_CHARACTERS_VALUES.map((value) => ({
                        disabled: props.disabledCodes.has(value),
                        text: value,
                      }))}
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
              )}
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
                    isInvalid={description === ""}
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
          disabled={description === ""}
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

export default SubfieldModal;
