import type { FC } from "react";
import React from "react";
import type { FormFieldEditorState } from "../queries";
import type { WithOnChange } from "./types";
import { EuiFormRow, EuiSwitch } from "@elastic/eui";
import { FormattedMessage } from "react-intl";

const CollapsedSwitchFormField: FC<FormFieldEditorState & WithOnChange> = (
  props,
) => {
  const { collapsed, onChange } = props;

  return (
    <EuiFormRow
      label={
        <FormattedMessage
          id="administration.customization.marc.datafield.collapsed"
          defaultMessage="Recolhido"
        />
      }
      hasChildLabel={false}
    >
      <EuiSwitch
        showLabel={false}
        label={
          <FormattedMessage
            id="administration.customization.datafield.collapsed"
            defaultMessage="Recolhido"
          />
        }
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
