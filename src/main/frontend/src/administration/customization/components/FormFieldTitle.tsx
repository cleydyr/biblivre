import type { FC } from "react";
import type { DatafieldTag } from "../types";
import { getFieldNameTranslation } from "./translations_helpers";

const FormFieldTitle: FC<{
  tag: DatafieldTag;
  translations: Record<string, string>;
}> = ({ tag, translations }) => {
  return `${tag} - ${getFieldNameTranslation(translations, tag)}`;
};

export default FormFieldTitle;
