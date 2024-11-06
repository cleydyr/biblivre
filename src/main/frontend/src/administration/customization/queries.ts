import type { FormData, RecordType } from "../../generated-sources";
import {
  DefaultApi,
  FormDataApi,
  SubfieldToJSON,
} from "../../generated-sources";
import { useGenericQuery } from "../../generic";
import type { UseMutationResult } from "@tanstack/react-query";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type {
  DeleteDatafieldPayload,
  FormDataLegacy,
  FormDataPayload,
  NonSuccessMessage,
  SuccessMessage,
} from "./types";
import { fetchJSONFromServer } from "../../api/legacy/helpers";

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
