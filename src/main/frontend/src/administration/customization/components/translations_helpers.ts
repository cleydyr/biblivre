import type {
  FormFieldEditorState,
  IndicatorCode,
  IndicatorOrder,
} from "./types";
import type { DatafieldTag } from "../types";
import type { Subfield } from "../../../generated-sources";
import { toIndicatorCode } from "../lib";

const TRANSLATION_KEY_PREFIX = `marc.bibliographic.datafield`;

export function getTranslationKeyPrefix(tag: string): string {
  return `${TRANSLATION_KEY_PREFIX}.${tag}`;
}

export function getSubfieldTranslationKeyPrefix(
  datafieldTag: string,
  subfieldCode: string,
) {
  return `${getTranslationKeyPrefix(datafieldTag)}.subfield.${subfieldCode}`;
}

export function getIndicatorTranslationKey(
  datafieldTag: string,
  order: IndicatorOrder,
) {
  return `${getTranslationKeyPrefix(datafieldTag)}.indicator.${order + 1}`;
}

export function getIndicatorCodeTranslationKey(
  datafieldTag: string,
  order: IndicatorOrder,
  code: IndicatorCode,
) {
  return `${getTranslationKeyPrefix(datafieldTag)}.indicator.${order + 1}.${code}`;
}

export function getFieldNameTranslation(
  translations: Record<string, string>,
  tag: DatafieldTag,
) {
  return translations[getTranslationKeyPrefix(tag)];
}

export function getSubfieldTranslation(
  translations: Record<string, string>,
  subfield: Subfield,
): string {
  return translations[
    getSubfieldTranslationKeyPrefix(subfield.datafield, subfield.subfield)
  ];
}

export function getTranslations(
  formFieldEditorState: FormFieldEditorState,
): Record<string, string> {
  const subfieldCodesTranslations = formFieldEditorState.subfields.reduce(
    (acc, subfield) => ({
      ...acc,
      [getSubfieldTranslationKeyPrefix(
        formFieldEditorState.tag,
        subfield.code,
      )]: subfield.description,
    }),
    {},
  );

  const indicator1Translations = Object.entries(
    formFieldEditorState.indicatorsState[0].translations,
  ).reduce(
    (acc, [code, translation]) => {
      return {
        ...acc,
        [getIndicatorCodeTranslationKey(
          formFieldEditorState.tag,
          0,
          toIndicatorCode(code),
        )]: translation,
      };
    },
    {} as Record<string, string>,
  );

  const indicator2Translations = Object.entries(
    formFieldEditorState.indicatorsState[1].translations,
  ).reduce(
    (acc, [code, translation]) => {
      return {
        ...acc,
        [getIndicatorCodeTranslationKey(
          formFieldEditorState.tag,
          1,
          toIndicatorCode(code),
        )]: translation,
      };
    },
    {} as Record<string, string>,
  );

  return {
    [getTranslationKeyPrefix(formFieldEditorState.tag)]:
      formFieldEditorState.name,
    [getIndicatorTranslationKey(formFieldEditorState.tag, 0)]:
      formFieldEditorState.indicatorsState[0].description,
    [getIndicatorTranslationKey(formFieldEditorState.tag, 1)]:
      formFieldEditorState.indicatorsState[1].description,
    ...subfieldCodesTranslations,
    ...indicator1Translations,
    ...indicator2Translations,
  };
}
