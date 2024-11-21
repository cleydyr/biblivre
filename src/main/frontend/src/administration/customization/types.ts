import type { FormData, RecordType } from "../../generated-sources";
import type { Digit } from "../../types";
import type { AlphaNumericCharacter } from "./components/types";

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

export type LegacyFormData = Omit<
  FormData,
  "materialType" | "indicator1" | "indicator2"
> & {
  materialType: string;
  indicator1: string;
  indicator2: string;
};

interface FormDataItem {
  formtab: LegacyFormData;
  translations: Record<string, string>;
}

export type FormDataPayload = Record<string, FormDataItem>;

export type DatafieldTag = `${Digit}${Digit}${Digit}`;

export type DeleteDatafieldPayload = {
  datafield: DatafieldTag;
  recordType: RecordType;
};

export type SubfieldCode = AlphaNumericCharacter;

export * from "../../types";
