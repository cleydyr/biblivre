import type { RecordType } from "../../generated-sources";
import { fetchJSONFromServer } from "../../api/legacy/helpers";
import type { FormDataPayload } from "./types";

export async function deleteFormDatafield(
  datafield: string,
  recordType: RecordType,
) {
  return fetchJSONFromServer(
    "administration.customization",
    "delete_form_datafield",
    {
      datafield,
      record_type: recordType,
    },
  );
}

export async function upsertFormDatafields(
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
