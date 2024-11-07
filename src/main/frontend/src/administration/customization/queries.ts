import type {
  AutocompleteType,
  FormData,
  MaterialType,
  RecordType,
  Subfield,
} from "../../generated-sources";
import {
  DefaultApi,
  FormDataApi,
  SubfieldToJSON,
} from "../../generated-sources";
import { useGenericQuery } from "../../generic";
import type { UseMutationResult, UseQueryResult } from "@tanstack/react-query";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type {
  DeleteDatafieldPayload,
  FormDataLegacy,
  FormDataPayload,
  NonSuccessMessage,
  SuccessMessage,
} from "./types";
import { fetchJSONFromServer } from "../../api/legacy/helpers";
import { getFieldNameTranslation } from "./lib";

const TRANSLATIONS_QUERY_KEY = "TRANSLATIONS";

function getTranslationsQueryKey(language: string) {
  return [TRANSLATIONS_QUERY_KEY, language];
}

export function useTranslationsQuery(language: string) {
  return useGenericQuery(
    DefaultApi,
    getTranslationsQueryKey(language),
    (api) => api.getTranslations,
    [{ language }],
  );
}

function getFormDataQueryKey(recordType: RecordType) {
  return ["FORM_DATA", recordType];
}

export function useFormDataQuery(recordType: RecordType) {
  return useGenericQuery(
    FormDataApi,
    getFormDataQueryKey(recordType),
    (api) => api.getFormData,
    [{ recordType }],
  );
}

async function upsertFormDatafields(
  fields: FormDataPayload,
  recordType: RecordType,
) {
  return fetchJSONFromServer(
    "administration.customization",
    "save_form_datafields",
    {
      fields: JSON.stringify(fields),
      record_type: recordType,
    },
  );
}

async function deleteFormDatafield(datafield: string, recordType: RecordType) {
  return fetchJSONFromServer(
    "administration.customization",
    "delete_form_datafield",
    {
      datafield,
      record_type: recordType,
    },
  );
}

export function useSaveFormDataFieldsMutation(): UseMutationResult<
  SuccessMessage,
  NonSuccessMessage,
  {
    fields: FormData[];
    recordType: RecordType;
  }
> {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ fields, recordType }) =>
      upsertFormDatafields(toFormDataPayload(fields), recordType),
    onSuccess: async (_, variables) => {
      await queryClient.invalidateQueries({
        queryKey: getFormDataQueryKey(variables.recordType),
      });
    },
  });
}

export function useDeleteFormDataFieldMutation(): UseMutationResult<
  SuccessMessage,
  NonSuccessMessage,
  DeleteDatafieldPayload
> {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ datafield, recordType }) =>
      deleteFormDatafield(datafield, recordType),
    onSuccess: async (_, variables) => {
      await queryClient.invalidateQueries({
        queryKey: getFormDataQueryKey(variables.recordType),
      });
    },
  });
}

function toFormDataPayload(formData: FormData[]): FormDataPayload {
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

function formDataToFormDataLegacyAdapter(formData: FormData): FormDataLegacy {
  return {
    ...formData,
    indicator1: stringJoiner(formData.indicator1),
    indicator2: stringJoiner(formData.indicator2),
    materialType: stringJoiner(formData.materialType),
    subfields: formData.subfields.map((subfield) => SubfieldToJSON(subfield)),
  };
}

function stringJoiner<T>(array: T[] | undefined): string {
  return array?.join(",") ?? "";
}

function getIndicator1Translations(
  { indicator1, datafield }: FormData,
  translations: Record<string, string>,
) {
  return getIndicatorTranslations(indicator1, datafield, translations);
}

function getIndicator2Translations(
  { indicator2, datafield }: FormData,
  translations: Record<string, string>,
) {
  return getIndicatorTranslations(indicator2, datafield, translations);
}

function getIndicatorTranslations(
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
            `marc.bibliographic.datafield.${datafield}.indicator.1.${code}`
          ],
      }),
      {} as Record<number, string>,
    ) ?? {}
  );
}

function getSubfieldTranslation(
  translations: Record<string, string>,
  subfield: Subfield,
): string {
  return translations[
    `marc.bibliographic.datafield.${subfield.datafield}.subfield.${subfield.subfield}`
  ];
}

export interface FormFieldEditorState {
  name: string;
  tag: string;
  repeatable: boolean;
  collapsed: boolean;
  indicator1Defined: boolean;
  indicator2Defined: boolean;
  indicator1Name: string;
  indicator2Name: string;
  indicator1ValueTranslations: Record<number, string>;
  indicator2ValueTranslations: Record<number, string>;
  materialTypes: MaterialType[];
  subfields: SubfieldState[];
}

interface SubfieldState {
  code: string;
  name: string;
  repeatable: boolean;
  collapsed: boolean;
  autocompleteType: AutocompleteType;
}

function toSubfieldState(
  translations: Record<string, string>,
  subfield: Subfield,
): SubfieldState {
  return {
    code: subfield.subfield ?? "",
    collapsed: subfield.collapsed ?? false,
    name: getSubfieldTranslation(translations, subfield),
    repeatable: subfield.repeatable ?? false,
    autocompleteType: subfield.autocompleteType ?? "disabled",
  };
}

function toFormFieldEditorState(
  translations: Record<string, string>,
  field: FormData,
): FormFieldEditorState {
  return {
    name: getFieldNameTranslation(translations, field.datafield),
    materialTypes: field.materialType,
    indicator1Defined: (field.indicator1?.length ?? 0) > 0,
    indicator2Defined: (field.indicator2?.length ?? 0) > 0,
    repeatable: field.repeatable,
    collapsed: field.collapsed,
    tag: field.datafield,
    indicator1Name:
      translations[
        `marc.bibliographic.datafield.${field.datafield}.indicator.1`
      ],
    indicator2Name:
      translations[
        `marc.bibliographic.datafield.${field.datafield}.indicator.2`
      ],
    indicator1ValueTranslations: getIndicator1Translations(field, translations),
    indicator2ValueTranslations: getIndicator2Translations(field, translations),
    subfields: field.subfields.map((subfield) =>
      toSubfieldState(translations, subfield),
    ),
  };
}

export function useFormFieldEditorState(
  language: string,
  field: FormData,
): UseQueryResult<FormFieldEditorState> {
  return useGenericQuery(
    DefaultApi,
    ["FORM_FIELDS", language, field.datafield],
    (api) => {
      return async (...args: Parameters<typeof api.getTranslations>) => {
        const translations = await api.getTranslations(...args);

        return toFormFieldEditorState(translations, field);
      };
    },
    [{ language }],
  );
}
