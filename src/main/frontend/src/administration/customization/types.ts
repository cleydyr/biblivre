import type { FormData, RecordType } from "../../generated-sources";
import type { Digit } from "../../types";

export type SuccessMessage = {
  success: true;
  message: string;
  message_level: "success";
};

export type ActionResult = "success" | "warning" | "error" | "normal";

export type NonSuccessMessage = {
  success: false;
  message: string;
  message_level: Exclude<ActionResult, "success" | "normal">;
};

export type FormDataLegacy = Omit<
  FormData,
  "materialType" | "indicator1" | "indicator2"
> & {
  materialType: string;
  indicator1: string;
  indicator2: string;
};

export interface FormDataItem {
  formtab: FormDataLegacy;
  translations: Record<string, string>;
}

export type FormDataPayload = Record<string, FormDataItem>;

export type DatafieldTag = `${Digit}${Digit}${Digit}`;

export type DeleteDatafieldPayload = {
  datafield: DatafieldTag;
  recordType: RecordType;
};

export function isDatafieldTag(s: string): s is DatafieldTag {
  return /^[0-9]{3}$/.test(s);
}

export * from "../../types";
