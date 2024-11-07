import type { FC } from "react";
import React, { Fragment, useEffect, useState } from "react";

import type { EuiSelectableOption } from "@elastic/eui";
import { EuiBasicTable } from "@elastic/eui";
import {
  EuiFieldText,
  EuiForm,
  EuiFormRow,
  EuiSelectable,
  EuiSpacer,
  EuiSwitch,
} from "@elastic/eui";
import { FormattedMessage } from "react-intl";
import type { FormData, MaterialType } from "../../generated-sources";
import { MaterialType as MaterialTypeEnum } from "../../generated-sources";
import type { FormFieldEditorState } from "./queries";
import { useFormFieldEditorState, useTranslationsQuery } from "./queries";
import type { Singleton } from "./types";
import type { EuiBasicTableColumn } from "@elastic/eui/src/components/basic_table/basic_table";

type WithOnChange = {
  onChange: (state: OnChangeParams) => void;
};

type OnChangeParams = Singleton<FormFieldEditorState>;

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

const IndicatorsFormSection: FC<FormFieldEditorState & WithOnChange> = ({
  indicator1Name,
  indicator1Defined,
  indicator1ValueTranslations,
  onChange,
}) => {
  type IndicatorValueNameInfo = {
    code: string;
    description: string;
  };

  const items: IndicatorValueNameInfo[] = Object.entries(
    indicator1ValueTranslations,
  ).map(([code, description]) => ({
    code,
    description,
  }));

  const columns: Array<EuiBasicTableColumn<IndicatorValueNameInfo>> = [
    {
      field: "code",
      name: (
        <FormattedMessage
          id="administration.customization.indicator.code"
          defaultMessage="Código"
        />
      ),
    },
    {
      field: "description",
      name: (
        <FormattedMessage
          id="administration.customization.indicator.description"
          defaultMessage="Descrição"
        />
      ),
    },
  ];

  return (
    <Fragment>
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
          checked={indicator1Defined}
          onChange={() => {
            onChange({
              indicator1Defined: !indicator1Defined,
            });
          }}
        />
      </EuiFormRow>
      <EuiFormRow
        label={
          <FormattedMessage
            id="administration.customization.indicator.1.name"
            defaultMessage="Nome do indicador 1"
          />
        }
      >
        <EuiFieldText
          disabled={!indicator1Defined}
          name="indicator1_name"
          value={indicator1Name}
        ></EuiFieldText>
      </EuiFormRow>
      <EuiBasicTable<IndicatorValueNameInfo>
        tableCaption="Valores do indicador 1"
        items={items}
        rowHeader="code"
        columns={columns}
      />
    </Fragment>
  );
};

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

const SubfieldSection: FC<FormFieldEditorState & WithOnChange> = () => {
  return null;
};

type FormFieldEditorProps = {
  field: FormData;
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

  if (!isSuccess || state === undefined) {
    return null;
  }

  const onChange = (data: OnChangeParams) => {
    setState({
      ...state,
      ...data,
    });
  };

  const props = {
    ...state,
    onChange,
  };

  return (
    <EuiForm component="form">
      <DatafieldTagFormField {...props} />
      <DatafieldNameFormField {...props} />
      <RepeatableSwitchFormField {...props} />
      <CollapsedSwitchFormField {...props} />
      <IndicatorsFormSection {...props} />
      <MaterialTypeSection {...props} />
      <SubfieldSection {...props} />
      <EuiSpacer size="xxl" />
    </EuiForm>
  );
};

export default FormFieldEditor;
