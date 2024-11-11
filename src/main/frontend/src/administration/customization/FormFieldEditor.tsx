import type { FC } from "react";
import React, { useEffect, useState } from "react";

import type { EuiSelectableOption, EuiBasicTableColumn } from "@elastic/eui";
import {
  EuiBasicTable,
  EuiCode,
  EuiFlexGroup,
  EuiFlexItem,
  EuiForm,
  EuiFormRow,
  EuiSelectable,
  EuiSpacer,
  EuiSwitch,
  EuiTabs,
  EuiTab,
} from "@elastic/eui";
import { FormattedMessage, useIntl } from "react-intl";
import type {
  AutocompleteType,
  FormData,
  MaterialType,
  Subfield,
} from "../../generated-sources";
import { MaterialType as MaterialTypeEnum } from "../../generated-sources";
import type { FormFieldEditorState } from "./queries";
import { useFormFieldEditorState, useTranslationsQuery } from "./queries";
import { autocompleteTypeMessageDescriptors } from "./messages";
import type { OnChangeParams, WithOnChange } from "./components/types";
import DatafieldTagFormField from "./components/DatafieldTagFormField";
import DatafieldNameFormField from "./components/DatafieldNameFormField";
import RepeatableSwitchFormField from "./components/RepeatableSwitchFormField";
import CollapsedSwitchFormField from "./components/CollapsedSwitchFormField";
import IndicatorFormSection from "./components/IndicatorFormSection";

const MaterialTypeSection: FC<FormFieldEditorState & WithOnChange> = ({
  materialTypes,
  onChange,
}) => {
  const { data: translations, isSuccess } = useTranslationsQuery("pt-BR");

  if (!isSuccess) {
    return null;
  }

  const allMaterialTypes: MaterialType[] = Object.values(MaterialTypeEnum);

  type WrappedMaterialType = {
    materialType: MaterialTypeEnum;
  };

  const options: EuiSelectableOption<WrappedMaterialType>[] =
    allMaterialTypes.map((materialType) => ({
      materialType,
      label: translations[`marc.material_type.${materialType}`],
      checked: materialTypes.includes(materialType) ? "on" : undefined,
    }));

  return (
    isSuccess && (
      <EuiFormRow
        label={
          <FormattedMessage
            id="administration.customization.material_type.title"
            defaultMessage="Tipos de Material"
          />
        }
      >
        <EuiSelectable<WrappedMaterialType>
          options={options}
          listProps={{ bordered: true }}
          onChange={(options) => {
            onChange({
              materialTypes: options
                .filter((option) => option.checked === "on")
                .map(({ materialType }) => materialType),
            });
          }}
        >
          {(list) => list}
        </EuiSelectable>
      </EuiFormRow>
    )
  );
};

function yesOrNoMessage(value: boolean) {
  return value ? (
    <FormattedMessage id="yes" defaultMessage="Sim" />
  ) : (
    <FormattedMessage id="no" defaultMessage="Não" />
  );
}

const SubfieldSection: FC<FormFieldEditorState & WithOnChange> = ({
  subfields,
}) => {
  const { formatMessage } = useIntl();

  const columns: Array<EuiBasicTableColumn<Subfield>> = [
    {
      field: "code",
      name: (
        <FormattedMessage
          id="administration.customization.indicator.code"
          defaultMessage="Código"
        />
      ),
      render: (code: string) => <EuiCode>{code}</EuiCode>,
    },
    {
      field: "name",
      name: (
        <FormattedMessage
          id="administration.customization.indicator.description"
          defaultMessage="Descrição"
        />
      ),
    },
    {
      field: "repeatable",
      name: (
        <FormattedMessage
          id="administration.customization.indicator.repeatable"
          defaultMessage="Repetível"
        />
      ),
      render: yesOrNoMessage,
    },
    {
      field: "collapsed",
      name: (
        <FormattedMessage
          id="administration.customization.indicator.collapsed"
          defaultMessage="Recolhido"
        />
      ),
      render: yesOrNoMessage,
    },
    {
      field: "autocompleteType",
      name: (
        <FormattedMessage
          id="administration.customization.indicator.autocomplete_type"
          defaultMessage="Tipo de auto-completar"
        />
      ),
      render: (autoCompleteType: AutocompleteType) =>
        formatMessage(autocompleteTypeMessageDescriptors[autoCompleteType]),
    },
  ];

  return (
    <EuiBasicTable<Subfield>
      tableCaption="Subcampos"
      items={subfields}
      rowHeader="code"
      columns={columns}
    />
  );
};

type FormFieldEditorProps = {
  field: FormData;
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
          id="administration.customization.indicator1.defined"
          defaultMessage="Indicador 2 definido"
        />
      }
    >
      <EuiSwitch
        label={
          <FormattedMessage
            id="administration.customization.indicator1.defined"
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
              <MaterialTypeSection {...props} />
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
              id="administration.customization.indicator.1"
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
              id="administration.customization.indicator.1"
              defaultMessage="Subcampos"
            />
          ),
          content: <SubfieldSection {...props} />,
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

      <EuiSpacer size="xxl" />
    </EuiForm>
  );
};

export default FormFieldEditor;
