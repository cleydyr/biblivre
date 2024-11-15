import type { FC } from "react";
import { getFieldNameTranslation } from "./lib";
import type { DatafieldTag } from "../types";

const FormFieldTitle: FC<{
  tag: DatafieldTag;
  translations: Record<string, string>;
}> = ({ tag, translations }) => {
  return `${tag} - ${getFieldNameTranslation(translations, tag)}`;
};

export default FormFieldTitle;
