import type { FC } from "react";
import React from "react";
import type { FormFieldEditorState } from "../queries";
import { EuiFieldText, EuiFormRow } from "@elastic/eui";
import { FormattedMessage } from "react-intl";
import type { WithOnChange } from "./types";

const DatafieldTagFormField: FC<FormFieldEditorState & WithOnChange> = ({
  tag,
  onChange,
}) => {
  return (
    <EuiFormRow
      label={
        <FormattedMessage
          id="administration.customization.marc.datafield.tag"
          defaultMessage="Campo MARC"
        />
      }
    >
      <EuiFieldText
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