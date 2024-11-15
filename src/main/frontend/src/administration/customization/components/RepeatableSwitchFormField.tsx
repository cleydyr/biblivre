import type { FC } from "react";
import React from "react";
import type { FormFieldEditorState, WithOnChange } from "./types";
import { EuiFormRow, EuiSwitch } from "@elastic/eui";
import { useIntl } from "react-intl";
import messages from "./messages";

const RepeatableSwitchFormField: FC<FormFieldEditorState & WithOnChange> = (
  props,
) => {
  const { formatMessage } = useIntl();

  const { repeatable, onChange } = props;

  return (
    <EuiFormRow
      label={formatMessage(messages.repeatable)}
      hasChildLabel={false}
    >
      <EuiSwitch
        showLabel={false}
        label={formatMessage(messages.repeatable)}
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
