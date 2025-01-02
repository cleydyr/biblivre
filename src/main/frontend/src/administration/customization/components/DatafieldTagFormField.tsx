import type { FC } from "react";
import React from "react";
import { EuiFieldText, EuiFormRow } from "@elastic/eui";
import { FormattedMessage } from "react-intl";
import type { FormFieldEditorState, WithOnChange } from "./types";
import { isValidDatafieldTag } from "../lib";

const DatafieldTagFormField: FC<
  FormFieldEditorState &
    WithOnChange & {
      mode: "edit" | "create";
    }
> = ({ tag, mode, onChange }) => {
  return (
    <EuiFormRow
      label={
        <FormattedMessage
          id="administration.customization.marc.datafield.tag"
          defaultMessage="Campo MARC"
        />
      }
      isInvalid={!isValidDatafieldTag(tag)}
      error={
        <FormattedMessage
          id="administration.customization.marc.datafield.error.invalid.tag"
          defaultMessage="O valor do campo Marc deve conter exatamente três digitos"
        />
      }
    >
      <EuiFieldText
        isInvalid={!isValidDatafieldTag(tag)}
        readOnly={mode === "edit"}
        name="datafield"
        maxLength={3}
        minLength={3}
        value={tag}
        onChange={(event) => {
          onChange({
            tag: event.target.value,
          });
        }}
      />
    </EuiFormRow>
  );
};

export default DatafieldTagFormField;
