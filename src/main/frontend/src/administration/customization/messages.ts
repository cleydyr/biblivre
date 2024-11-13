import { defineMessages } from "react-intl";
import type { AutocompleteType } from "../../generated-sources";
import { AutocompleteType as AutocompleteTypeEnum } from "../../generated-sources";

export const autocompleteTypeMessageDescriptors =
  defineMessages<AutocompleteType>({
    [AutocompleteTypeEnum.Disabled]: {
      id: "subfield.autocomplete.type.disabled",
      defaultMessage: "Desabilitado",
    },
    [AutocompleteTypeEnum.PreviousValues]: {
      id: "subfield.autocomplete.type.previous_values",
      defaultMessage: "Valores anteriores",
    },
    [AutocompleteTypeEnum.FixedTable]: {
      id: "subfield.autocomplete.type.fixed_table",
      defaultMessage: "Tabela fixa",
    },
    [AutocompleteTypeEnum.Authorities]: {
      id: "subfield.autocomplete.type.fixed_table",
      defaultMessage: "Tabela fixa",
    },
    [AutocompleteTypeEnum.Biblio]: {
      id: "subfield.autocomplete.type.fixed_table",
      defaultMessage: "Tabela fixa",
    },
    [AutocompleteTypeEnum.Vocabulary]: {
      id: "subfield.autocomplete.type.fixed_table",
      defaultMessage: "Tabela fixa",
    },
    [AutocompleteTypeEnum.FixedTableWithPreviousValues]: {
      id: "subfield.autocomplete.type.fixed_table",
      defaultMessage: "Tabela fixa",
    },
  });

const messages = defineMessages({
  editIndicatorValueDescription: {
    id: "administration.customization.indicator.action.edit.description",
    defaultMessage: "Editar valor {value} do indicador {indicator}",
  },
  removeIndicatorValueDescription: {
    id: "administration.customization.indicator.action.remove.description",
    defaultMessage: "Remover valor {value} do indicador {indicator}",
  },
});

export default messages;
