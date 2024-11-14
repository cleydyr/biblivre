import type { FC } from "react";
import React from "react";
import type { FormFieldEditorState } from "../queries";
import { useTranslationsQuery } from "../queries";
import type { WithOnChange } from "./types";
import type { MaterialType } from "../../../generated-sources";
import { MaterialType as MaterialTypeEnum } from "../../../generated-sources";
import type { EuiSelectableOption } from "@elastic/eui";
import { EuiFormRow, EuiSelectable } from "@elastic/eui";
import { FormattedMessage } from "react-intl";

const MaterialTypeFormSection: FC<FormFieldEditorState & WithOnChange> = ({
  materialTypes,
  onChange,
}) => {
  const { data: translations, isSuccess } = useTranslationsQuery("pt-BR");

  if (!isSuccess) {
    return null;
  }

  const allMaterialTypes: MaterialType[] = Object.values(MaterialTypeEnum);

  const options: EuiSelectableOption[] = allMaterialTypes.map(
    (materialType) => ({
      label: translations[`marc.material_type.${materialType}`],
      checked: materialTypes.includes(materialType) ? "on" : undefined,
      key: materialType,
    }),
  );

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
        <EuiSelectable
          options={options}
          listProps={{ bordered: true }}
          onChange={(options) => {
            onChange({
              materialTypes: options
                .filter(
                  (option) =>
                    option.checked === "on" && option.key !== undefined,
                )
                .map((option) => option.key as MaterialType),
            });
          }}
        >
          {(list) => {
            return list;
          }}
        </EuiSelectable>
      </EuiFormRow>
    )
  );
};

export default MaterialTypeFormSection;
