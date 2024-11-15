import type { FormData, Subfield } from "../../../generated-sources";
import { SubfieldToJSON } from "../../../generated-sources";
import type {
  FormFieldEditorState,
  IndicatorOrder,
  SubfieldFormEditorState,
} from "./types";
import { toDatafieldTag, toIndicatorCode, toSubfieldCode } from "../lib";

import type { FormDataLegacy, FormDataPayload } from "../types";
import {
  getFieldNameTranslation,
  getIndicatorCodeTranslationKey,
  getIndicatorTranslationKey,
  getSubfieldTranslation,
  getTranslations,
} from "./translations_helpers";

function toSubfieldState(
  translations: Record<string, string>,
  subfield: Subfield,
): SubfieldFormEditorState {
  return {
    code: toSubfieldCode(subfield.subfield),
    collapsed: subfield.collapsed ?? false,
    description: getSubfieldTranslation(translations, subfield),
    repeatable: subfield.repeatable ?? false,
    autocompleteType: subfield.autocompleteType ?? "disabled",
    sortOrder: subfield.sortOrder,
  };
}

export function toFormFieldEditorState(
  translations: Record<string, string>,
  field: FormData,
): FormFieldEditorState {
  return {
    name: getFieldNameTranslation(
      translations,
      toDatafieldTag(field.datafield),
    ),
    materialTypes: field.materialType,
    indicatorsState: [
      {
        defined: (field.indicator1?.length ?? 0) > 0,
        description:
          translations[getIndicatorTranslationKey(field.datafield, 0)],
        translations: getIndicator1Translations(field, translations),
      },
      {
        defined: (field.indicator2?.length ?? 0) > 0,
        description:
          translations[getIndicatorTranslationKey(field.datafield, 1)],
        translations: getIndicator2Translations(field, translations),
      },
    ],
    repeatable: field.repeatable,
    collapsed: field.collapsed,
    tag: field.datafield,
    subfields: field.subfields.map((subfield) =>
      toSubfieldState(translations, subfield),
    ),
  };
}

function stringJoiner<T>(array: T[] | undefined): string {
  return array?.join(",") ?? "";
}

function getIndicator1Translations(
  { indicator1, datafield }: FormData,
  translations: Record<string, string>,
) {
  return getIndicatorTranslations(0, indicator1, datafield, translations);
}

function getIndicator2Translations(
  { indicator2, datafield }: FormData,
  translations: Record<string, string>,
) {
  return getIndicatorTranslations(1, indicator2, datafield, translations);
}

function getIndicatorTranslations(
  order: IndicatorOrder,
  indicator: string[] | undefined,
  datafield: string,
  translations: Record<string, string>,
) {
  return (
    indicator?.reduce(
      (acc, code) => ({
        ...acc,
        [Number(code)]:
          translations[
            getIndicatorCodeTranslationKey(
              datafield,
              order,
              toIndicatorCode(code),
            )
          ],
      }),
      {} as Record<number, string>,
    ) ?? {}
  );
}

export function fromFormDataToFormDataPayload(
  formData: FormData[],
): FormDataPayload {
  return formData.reduce(
    (acc, cur) => ({
      ...acc,
      [cur.datafield]: {
        formtab: formDataToFormDataLegacyAdapter(cur),
        translations: {},
      },
    }),
    {} as FormDataPayload,
  );
}

export function fromFormFieldEditorStateToFormDataPayload(
  formFieldEditorState: FormFieldEditorState,
): FormDataPayload {
  const partialPayload = fromFormDataToFormDataPayload([
    {
      datafield: formFieldEditorState.tag,
      materialType: formFieldEditorState.materialTypes,
      indicator1: formFieldEditorState.indicatorsState[0].defined
        ? Object.keys(formFieldEditorState.indicatorsState[0].translations)
        : [],
      indicator2: formFieldEditorState.indicatorsState[1].defined
        ? Object.keys(formFieldEditorState.indicatorsState[1].translations)
        : [],
      subfields: formFieldEditorState.subfields.map((subfield) => ({
        datafield: formFieldEditorState.tag,
        subfield: subfield.code,
        repeatable: subfield.repeatable,
        collapsed: subfield.collapsed,
        sortOrder: subfield.sortOrder,
        autocompleteType: subfield.autocompleteType,
      })),
      repeatable: formFieldEditorState.repeatable,
      collapsed: formFieldEditorState.collapsed,
      sortOrder: formFieldEditorState.subfields[0].sortOrder,
    },
  ]);

  partialPayload[formFieldEditorState.tag].translations =
    getTranslations(formFieldEditorState);

  return partialPayload;
}

function formDataToFormDataLegacyAdapter(formData: FormData): FormDataLegacy {
  return {
    ...formData,
    indicator1: stringJoiner(formData.indicator1),
    indicator2: stringJoiner(formData.indicator2),
    materialType: stringJoiner(formData.materialType),
    subfields: formData.subfields.map((subfield) => SubfieldToJSON(subfield)),
  };
}
