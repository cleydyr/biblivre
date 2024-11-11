import type { FC } from "react";
import React from "react";
import type { FormFieldEditorState } from "../queries";
import type { WithOnChange } from "./types";
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
    >
      <EuiFieldText
        name="name"
        value={name}
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
