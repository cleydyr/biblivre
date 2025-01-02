import type { FC } from "react";
import React from "react";
import type { FormFieldEditorState, WithOnChange } from "./types";
import { EuiFieldText, EuiFormRow } from "@elastic/eui";
import { FormattedMessage } from "react-intl";

const DatafieldNameFormField: FC<FormFieldEditorState & WithOnChange> = (
  props,
) => {
  const { name, onChange } = props;

  return (
    <EuiFormRow
      label={
        <FormattedMessage
          id="administration.customization.marc.datafield.name"
          defaultMessage="Nome do Campo"
        />
      }
      isInvalid={name.length === 0}
      error={
        <FormattedMessage
          id="administration.customization.marc.datafield.error.invalid.name"
          defaultMessage="O campo não pode ser vazio"
        />
      }
    >
      <EuiFieldText
        name="name"
        value={name}
        isInvalid={name.length === 0}
        onChange={(event) => {
          onChange({
            name: event.target.value,
          });
        }}
      />
    </EuiFormRow>
  );
};

export default DatafieldNameFormField;
