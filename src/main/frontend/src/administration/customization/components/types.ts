import type { Singleton } from "../../../types";
import type { FormFieldEditorState } from "../queries";

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
