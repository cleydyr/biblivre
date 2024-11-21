import type { FC } from "react";
import type { DatafieldTag } from "../types";
import { getDatafieldNameTranslation } from "./translations_helpers";

const FormFieldTitle: FC<{
  tag: DatafieldTag;
  translations: TranslationTable;
}> = ({ tag, translations }) => {
  return `${tag} - ${getDatafieldNameTranslation(translations, tag)}`;
};

export default FormFieldTitle;
