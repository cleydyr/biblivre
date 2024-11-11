import type { FC } from "react";
import React from "react";
import type { FormFieldEditorState } from "../queries";
import type { WithOnChange } from "./types";
import { EuiFormRow, EuiSwitch } from "@elastic/eui";
import { FormattedMessage } from "react-intl";

const RepeatableSwitchFormField: FC<FormFieldEditorState & WithOnChange> = (
  props,
) => {
  const { repeatable, onChange } = props;

  return (
    <EuiFormRow
      label={
        <FormattedMessage
          id="administration.customization.marc.datafield.repeatable"
          defaultMessage="Repetível"
        />
      }
      hasChildLabel={false}
    >
      <EuiSwitch
        showLabel={false}
        label={
          <FormattedMessage
            id="administration.customization.marc.datafield.repeatable"
            defaultMessage="Repetível"
          />
        }
        checked={repeatable}
        onChange={() =>
          onChange({
            repeatable: !repeatable,
          })
        }
      />
    </EuiFormRow>
  );
};

export default RepeatableSwitchFormField;
