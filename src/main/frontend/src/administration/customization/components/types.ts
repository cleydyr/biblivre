import type { Singleton } from "../../../types";
import type { FormFieldEditorState } from "../queries";

export type WithOnChange = {
  onChange: (state: OnChangeParams) => void;
};

export type OnChangeParams = Singleton<FormFieldEditorState>;

export const INDICATOR_CODE_VALUES = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9] as const;

export type IndicatorCode = (typeof INDICATOR_CODE_VALUES)[number];

export type IndicatorOrder = 0 | 1;
