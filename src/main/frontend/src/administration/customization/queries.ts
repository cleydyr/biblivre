import type { FormData, RecordType } from "../../generated-sources";
import { DefaultApi, FormDataApi } from "../../generated-sources";
import { useGenericQuery } from "../../generic";
import type { UseMutationResult } from "@tanstack/react-query";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type {
  DeleteDatafieldPayload,
  NonSuccessMessage,
  SuccessMessage,
} from "./types";

import type { FormFieldEditorState } from "./components/types";
import {
  fromFormDataToFormDataPayload,
  fromFormFieldEditorStateToFormDataPayload,
} from "./components/lib";
import { deleteFormDatafield, upsertFormDatafields } from "./legacy-api";

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

export function useSaveFormDataFieldsMutation2(): UseMutationResult<
  SuccessMessage,
  NonSuccessMessage,
  {
    formFieldEditorState: FormFieldEditorState;
    recordType: RecordType;
  }
> {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ formFieldEditorState, recordType }) =>
      upsertFormDatafields(
        fromFormFieldEditorStateToFormDataPayload(formFieldEditorState),
        recordType,
      ),
    onSuccess: async (_, variables) => {
      await queryClient.invalidateQueries({
        queryKey: getFormDataQueryKey(variables.recordType),
      });
    },
  });
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
      upsertFormDatafields(fromFormDataToFormDataPayload(fields), recordType),
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
