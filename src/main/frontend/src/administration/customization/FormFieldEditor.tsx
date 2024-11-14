import type { FC } from "react";
import React, { useEffect, useState } from "react";

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
import { FormattedMessage } from "react-intl";
import type { FormData } from "../../generated-sources";
import type { FormFieldEditorState } from "./queries";
import { useFormFieldEditorState } from "./queries";
import type { OnChangeParams, WithOnChange } from "./components/types";
import DatafieldTagFormField from "./components/DatafieldTagFormField";
import DatafieldNameFormField from "./components/DatafieldNameFormField";
import RepeatableSwitchFormField from "./components/RepeatableSwitchFormField";
import CollapsedSwitchFormField from "./components/CollapsedSwitchFormField";
import IndicatorFormSection from "./components/IndicatorFormSection";
import MaterialTypeFormSection from "./components/MaterialTypeFormSection";
import SubfieldFormSection from "./components/SubfieldFormSection";

type FormFieldEditorProps = {
  field: FormData;
  onSave: () => void;
};

const Indicator1FormSwitch: FC<FormFieldEditorState & WithOnChange> = ({
  indicatorsState,
  onChange,
}) => {
  const [{ defined }] = indicatorsState;

  return (
    <EuiFormRow
      label={
        <FormattedMessage
          id="administration.customization.indicator1.defined"
          defaultMessage="Indicador 1 definido"
        />
      }
    >
      <EuiSwitch
        label={
          <FormattedMessage
            id="administration.customization.indicator1.defined"
            defaultMessage="Indicador 1 definido"
          />
        }
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

  return (
    <EuiFormRow
      label={
        <FormattedMessage
          id="administration.customization.indicator2.defined"
          defaultMessage="Indicador 2 definido"
        />
      }
    >
      <EuiSwitch
        label={
          <FormattedMessage
            id="administration.customization.indicator2.defined"
            defaultMessage="Indicador 2 definido"
          />
        }
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

const FormFieldEditor: FC<FormFieldEditorProps> = ({ field }) => {
  const { data: formFieldEditorState, isSuccess } = useFormFieldEditorState(
    "pt-BR",
    field,
  );

  const [state, setState] = useState(formFieldEditorState);

  useEffect(() => {
    if (formFieldEditorState !== undefined) {
      setState(formFieldEditorState);
    }
  }, [formFieldEditorState]);

  const [selectedTabId, setSelectedTabId] = useState<string>("general");

  const onChange = (data: OnChangeParams) => {
    if (!state) {
      return;
    }

    setState({
      ...state,
      ...data,
    });
  };

  if (!isSuccess || state === undefined) {
    return null;
  }

  const props = {
    ...state,
    onChange,
  };

  const tabs = state
    ? [
        {
          id: "general",
          name: (
            <FormattedMessage
              id="administration.customization.indicator.general"
              defaultMessage="Geral"
            />
          ),
          content: (
            <EuiFlexGroup direction="column">
              <EuiFlexGroup>
                <EuiFlexItem grow={false}>
                  <DatafieldTagFormField {...props} />
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
          name: (
            <FormattedMessage
              id="administration.customization.indicator.1"
              defaultMessage="Indicador 1"
            />
          ),
          content: (
            <EuiFlexGroup direction="column">
              <Indicator1FormSwitch {...props} />
              {state.indicatorsState[0].defined && (
                <IndicatorFormSection {...props} order={0} />
              )}
            </EuiFlexGroup>
          ),
        },
        {
          id: "indicator2",
          name: (
            <FormattedMessage
              id="administration.customization.indicator.2"
              defaultMessage="Indicador 2"
            />
          ),
          content: (
            <EuiFlexGroup direction="column">
              <Indicator2FormSwitch {...props} />
              {state.indicatorsState[1].defined && (
                <IndicatorFormSection {...props} order={1} />
              )}
            </EuiFlexGroup>
          ),
        },
        {
          id: "subfields",
          name: (
            <FormattedMessage
              id="administration.customization.subfields"
              defaultMessage="Subcampos"
            />
          ),
          content: <SubfieldFormSection {...props} />,
        },
      ]
    : undefined;

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
