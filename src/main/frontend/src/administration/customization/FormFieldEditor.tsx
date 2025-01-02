import type { FC } from "react";
import React, { useState } from "react";

import {
  EuiFlexGroup,
  EuiFlexItem,
  EuiForm,
  EuiFormRow,
  EuiSpacer,
  EuiSwitch,
  EuiTabs,
  EuiTab,
} from "@elastic/eui";
import { defineMessages, useIntl } from "react-intl";
import type {
  FormFieldEditorState,
  OnChangeParams,
  WithOnChange,
} from "./components/types";
import DatafieldTagFormField from "./components/DatafieldTagFormField";
import DatafieldNameFormField from "./components/DatafieldNameFormField";
import RepeatableSwitchFormField from "./components/RepeatableSwitchFormField";
import CollapsedSwitchFormField from "./components/CollapsedSwitchFormField";
import IndicatorFormSection from "./components/IndicatorFormSection";
import MaterialTypeFormSection from "./components/MaterialTypeFormSection";
import SubfieldFormSection from "./components/SubfieldFormSection";

type FormFieldEditorProps = {
  editorState: FormFieldEditorState;
  onChange: (changedFormData: FormFieldEditorState) => void;
  mode: "edit" | "create";
};

const messages = defineMessages({
  general: {
    id: "administration.customization.indicator.general",
    defaultMessage: "Geral",
  },
  indicator: {
    id: "administration.customization.indicator",
    defaultMessage: "Indicador {order}",
  },
  subfields: {
    id: "administration.customization.subfields",
    defaultMessage: "Subcampos",
  },
  defined: {
    id: "administration.customization.indicator.defined",
    defaultMessage: "Indicador {order} definido",
  },
});

const Indicator1FormSwitch: FC<FormFieldEditorState & WithOnChange> = ({
  indicatorsState,
  onChange,
}) => {
  const [{ defined }] = indicatorsState;

  const { formatMessage } = useIntl();

  return (
    <EuiFormRow
      label={formatMessage(messages.defined, {
        order: 1,
      })}
    >
      <EuiSwitch
        label={formatMessage(messages.defined, {
          order: 1,
        })}
        showLabel={false}
        checked={defined}
        onChange={() => {
          onChange({
            indicatorsState: [
              {
                ...indicatorsState[0],
                defined: !defined,
              },
              indicatorsState[1],
            ],
          });
        }}
      />
    </EuiFormRow>
  );
};

const Indicator2FormSwitch: FC<FormFieldEditorState & WithOnChange> = ({
  indicatorsState,
  onChange,
}) => {
  const [, { defined }] = indicatorsState;

  const { formatMessage } = useIntl();

  return (
    <EuiFormRow
      label={formatMessage(messages.defined, {
        order: 2,
      })}
    >
      <EuiSwitch
        label={formatMessage(messages.defined, {
          order: 2,
        })}
        showLabel={false}
        checked={defined}
        onChange={() => {
          onChange({
            indicatorsState: [
              indicatorsState[0],
              {
                ...indicatorsState[1],
                defined: !defined,
              },
            ],
          });
        }}
      />
    </EuiFormRow>
  );
};

const FormFieldEditor: FC<FormFieldEditorProps> = ({
  editorState,
  onChange,
  mode,
}) => {
  const { formatMessage } = useIntl();

  const [selectedTabId, setSelectedTabId] = useState<string>("general");

  const handleFormFieldEditorState = (data: OnChangeParams) => {
    const newState = {
      ...editorState,
      ...data,
    };

    onChange(newState);
  };

  const props = {
    ...editorState,
    onChange: handleFormFieldEditorState,
  };

  const tabs = [
    {
      id: "general",
      name: formatMessage(messages.general),
      content: (
        <EuiFlexGroup direction="column">
          <EuiFlexGroup>
            <EuiFlexItem grow={false}>
              <DatafieldTagFormField mode={mode} {...props} />
            </EuiFlexItem>
            <EuiFlexItem grow={false}>
              <DatafieldNameFormField {...props} />
            </EuiFlexItem>
            <EuiFlexItem grow={false}>
              <RepeatableSwitchFormField {...props} />
            </EuiFlexItem>
            <EuiFlexItem grow={false}>
              <CollapsedSwitchFormField {...props} />
            </EuiFlexItem>
          </EuiFlexGroup>
          <MaterialTypeFormSection {...props} />
        </EuiFlexGroup>
      ),
    },
    {
      id: "indicator1",
      name: formatMessage(messages.indicator, { order: 1 }),
      content: (
        <EuiFlexGroup direction="column">
          <Indicator1FormSwitch {...props} />
          {editorState.indicatorsState[0].defined && (
            <IndicatorFormSection {...props} order={0} />
          )}
        </EuiFlexGroup>
      ),
    },
    {
      id: "indicator2",
      name: formatMessage(messages.indicator, { order: 2 }),
      content: (
        <EuiFlexGroup direction="column">
          <Indicator2FormSwitch {...props} />
          {editorState.indicatorsState[1].defined && (
            <IndicatorFormSection {...props} order={1} />
          )}
        </EuiFlexGroup>
      ),
    },
    {
      id: "subfields",
      name: formatMessage(messages.subfields),
      content: <SubfieldFormSection {...props} />,
    },
  ];

  const onSelectedTabChange = (tabId: string) => {
    setSelectedTabId(tabId);
  };

  const selectedTabContent = tabs?.find(
    (tab) => tab.id === selectedTabId,
  )?.content;

  return (
    <EuiForm component="form">
      <EuiTabs>
        {tabs?.map((tab, index) => (
          <EuiTab
            key={index}
            onClick={() => onSelectedTabChange(tab.id)}
            isSelected={tab.id === selectedTabId}
          >
            {tab.name}
          </EuiTab>
        ))}
      </EuiTabs>
      <EuiSpacer />
      {selectedTabContent}
    </EuiForm>
  );
};

export default FormFieldEditor;
