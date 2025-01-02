import type { DatafieldTag, SubfieldCode } from "./types";
import type { FormFieldEditorState, IndicatorCode } from "./components/types";
import { AutocompleteType, type FormData } from "../../generated-sources";

type Ordered = {
  sortOrder: number;
};

const DATAFIELD_TAG_REGEXP = /^[0-9]{3}$/;

export function getAffectedItems<T extends Ordered>(
  items: T[],
  source: number,
  destination: number,
): T[] {
  const lesser = Math.min(source, destination);

  const greater = Math.max(source, destination);

  const movedUpwards = lesser === destination;

  const sortOrderDelta = movedUpwards ? 1 : -1;

  const sliceBoundsDelta = movedUpwards ? 0 : 1;

  const displaced = items
    .slice(lesser + sliceBoundsDelta, greater + sliceBoundsDelta)
    .map((item: T) => ({
      ...item,
      sortOrder: item.sortOrder + sortOrderDelta,
    }));

  const movedItem = {
    ...items[source],
    sortOrder: destination,
  };

  return movedUpwards ? [movedItem, ...displaced] : [...displaced, movedItem];
}

export function toIndicatorCode(value: string): IndicatorCode {
  switch (value) {
    case "0":
      return 0;
    case "1":
      return 1;
    case "2":
      return 2;
    case "3":
      return 3;
    case "4":
      return 4;
    case "5":
      return 5;
    case "6":
      return 6;
    case "7":
      return 7;
    case "8":
      return 8;
    case "9":
      return 9;
    default:
      throw new Error(`Invalid value for ${value}`);
  }
}

export function toSubfieldCode(value: string): SubfieldCode {
  if (/^[a-z0-9]$/i.test(value)) {
    return value as SubfieldCode;
  }

  throw new Error(`Invalid value for ${value}`);
}

export function isValidDatafieldTag(value: string) {
  return DATAFIELD_TAG_REGEXP.test(value);
}

export function toDatafieldTag(value: string): DatafieldTag {
  if (isValidDatafieldTag(value)) {
    return value as DatafieldTag;
  }

  throw new Error(`Invalid tag value: ${value}`);
}

export const AUTOCOMPLETE_VALUES = Object.values(AutocompleteType);

export function toAutoCompleteType(value: string): AutocompleteType {
  if (AUTOCOMPLETE_VALUES.includes(value as AutocompleteType)) {
    return value as AutocompleteType;
  }

  throw new Error(`Invalid autocomplete type: ${value}`);
}

export function toPartialFormData(
  formFieldEditorState: FormFieldEditorState,
): Omit<FormData, "sortOrder"> {
  return {
    datafield: formFieldEditorState.tag,
    indicator1: formFieldEditorState.indicatorsState[0].defined
      ? Object.keys(formFieldEditorState.indicatorsState[0].translations)
      : [],
    indicator2: formFieldEditorState.indicatorsState[1].defined
      ? Object.keys(formFieldEditorState.indicatorsState[1].translations)
      : [],
    materialType: formFieldEditorState.materialTypes,
    subfields: formFieldEditorState.subfields.map(
      ({ code, sortOrder, autocompleteType, repeatable, collapsed }) => ({
        datafield: formFieldEditorState.tag,
        subfield: code,
        sortOrder,
        autocompleteType,
        repeatable,
        collapsed,
      }),
    ),
    repeatable: formFieldEditorState.repeatable,
    collapsed: formFieldEditorState.collapsed,
  };
}
