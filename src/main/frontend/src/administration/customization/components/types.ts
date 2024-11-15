import type { Singleton } from "../../../types";
import type {
  AutocompleteType,
  MaterialType,
} from "../../../generated-sources";
import type { SubfieldCode } from "../types";

export type WithOnChange = {
  onChange: (state: OnChangeParams) => void;
};

export type OnChangeParams = Singleton<FormFieldEditorState>;

export const INDICATOR_CODE_VALUES = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9] as const;

export type IndicatorCode = (typeof INDICATOR_CODE_VALUES)[number];

export type IndicatorOrder = 0 | 1;

export const ALPHANUMERIC_CHARACTERS_VALUES = [
  "a",
  "b",
  "c",
  "d",
  "e",
  "f",
  "g",
  "h",
  "i",
  "j",
  "k",
  "l",
  "m",
  "n",
  "o",
  "p",
  "q",
  "r",
  "s",
  "t",
  "u",
  "v",
  "w",
  "x",
  "y",
  "z",
  "0",
  "1",
  "2",
  "3",
  "4",
  "5",
  "6",
  "7",
  "8",
  "9",
] as const;

export type AlphaNumericCharacter =
  (typeof ALPHANUMERIC_CHARACTERS_VALUES)[number];

export interface IndicatorEditorState {
  defined: boolean;
  description: string;
  translations: Record<IndicatorCode, string>;
}

export interface FormFieldEditorState {
  name: string;
  tag: string;
  repeatable: boolean;
  collapsed: boolean;
  indicatorsState: [IndicatorEditorState, IndicatorEditorState];
  materialTypes: MaterialType[];
  subfields: SubfieldFormEditorState[];
}

export interface SubfieldFormEditorState {
  code: SubfieldCode;
  description: string;
  repeatable: boolean;
  collapsed: boolean;
  autocompleteType: AutocompleteType;
  sortOrder: number;
}
