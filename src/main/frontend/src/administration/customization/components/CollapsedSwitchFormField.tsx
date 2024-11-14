import type { FC } from "react";
import React from "react";
import type { FormFieldEditorState } from "../queries";
import type { WithOnChange } from "./types";
import { EuiFormRow, EuiSwitch } from "@elastic/eui";
import { useIntl } from "react-intl";
import messages from "./messages";

const CollapsedSwitchFormField: FC<FormFieldEditorState & WithOnChange> = (
  props,
) => {
  const { formatMessage } = useIntl();

  const { collapsed, onChange } = props;

  return (
    <EuiFormRow label={formatMessage(messages.collapsed)} hasChildLabel={false}>
      <EuiSwitch
        showLabel={false}
        label={formatMessage(messages.collapsed)}
        checked={collapsed}
        onChange={() => {
          onChange({
            collapsed: !collapsed,
          });
        }}
      />
    </EuiFormRow>
  );
};

export default CollapsedSwitchFormField;
