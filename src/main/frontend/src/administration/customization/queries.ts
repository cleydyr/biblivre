import {
  DefaultApi,
  FormDataApi,
  RecordType,
  FormData,
  ReportFillRequest,
  ReportFill,
  Configuration,
  ReportFillApi,
  SubfieldToJSON,
} from "../../generated-sources";
import { useGenericQuery } from "../../generic";
import {
  useMutation,
  UseMutationOptions,
  UseMutationResult,
} from "@tanstack/react-query";
import { baseEndpointPath, DEFAULT_FETCH_OPTIONS } from "../../util";

export function useTranslationQuery(language: string) {
  return useGenericQuery(
    DefaultApi,
    ["TRANSLATIONS", language],
    (api) => api.getTranslations,
    [{ language }],
  );
}

export function useFormDataQuery(recordType: RecordType) {
  return useGenericQuery(
    FormDataApi,
    ["FORM_DATA", recordType],
    (api) => api.getFormData,
    [{ recordType }],
  );
}

async function fetchJSONFromServer(
  module: string,
  action: string,
  otherParams: object,
) {
  const response = await fetch("/", {
    method: "POST",
    headers: {
      "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
    },
    body: new URLSearchParams({
      controller: "json",
      module,
      action,
      ...otherParams,
    }).toString(),
  });

  return response.json();
}

type FormDataLegacy = Omit<
  FormData,
  "materialType" | "indicator1" | "indicator2"
> & {
  materialType: string;
  indicator1: string;
  indicator2: string;
};

interface FormDataItem {
  formtab: FormDataLegacy;
  translations: Record<string, string>;
}

type FormDataPayload = Record<string, FormDataItem>;

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

type ActionResult = "success" | "warning" | "error" | "normal";

type SuccessMessage = {
  success: true;
  message: string;
  message_level: "success";
};

type NonSuccessMessage = {
  success: false;
  message: string;
  message_level: Exclude<ActionResult, "success" | "normal">;
};

export function useSaveFormDataFieldsMutation(): UseMutationResult<
  SuccessMessage,
  NonSuccessMessage,
  {
    fields: FormData[];
    recordType: RecordType;
  }
> {
  return useMutation({
    mutationFn: ({ fields, recordType }) =>
      upsertFormDatafields(toFormDataPayload(fields), recordType),
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
