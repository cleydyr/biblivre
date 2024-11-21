import type {
  FormFieldEditorState,
  IndicatorCode,
  IndicatorOrder,
} from "./types";
import type { DatafieldTag, TranslationTable } from "../types";
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
  return `${getIndicatorTranslationKey(datafieldTag, order)}.${code}`;
}

export function getDatafieldNameTranslation(
  translations: TranslationTable,
  tag: DatafieldTag,
) {
  return translations[getTranslationKeyPrefix(tag)];
}

export function getSubfieldNameTranslation(
  translations: TranslationTable,
  subfield: Subfield,
): string {
  return translations[
    getSubfieldTranslationKeyPrefix(subfield.datafield, subfield.subfield)
  ];
}

export function extractTranslations(
  formFieldEditorState: FormFieldEditorState,
): TranslationTable {
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

  const indicator1Translations: TranslationTable = Object.entries(
    formFieldEditorState.indicatorsState[0].translations,
  ).reduce((acc, [code, translation]) => {
    return {
      ...acc,
      [getIndicatorCodeTranslationKey(
        formFieldEditorState.tag,
        0,
        toIndicatorCode(code),
      )]: translation,
    };
  }, {} as TranslationTable);

  const indicator2Translations: TranslationTable = Object.entries(
    formFieldEditorState.indicatorsState[1].translations,
  ).reduce((acc, [code, translation]) => {
    return {
      ...acc,
      [getIndicatorCodeTranslationKey(
        formFieldEditorState.tag,
        1,
        toIndicatorCode(code),
      )]: translation,
    };
  }, {} as TranslationTable);

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
