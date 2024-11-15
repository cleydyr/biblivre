import type { FC } from "react";
import React from "react";

import { defineMessages, useIntl } from "react-intl";
import type { SubfieldCode } from "../types";
import type { SubfieldFormEditorState } from "./types";
import { ALPHANUMERIC_CHARACTERS_VALUES } from "./types";
import SubfieldModal from "./SubfieldModal";

type SubfieldCreateModalProps = {
  disabledCodes: Set<SubfieldCode>;
  onCloseModal: () => void;
  onConfirm: (subfield: SubfieldFormEditorState) => void;
};

const messages = defineMessages({
  title: {
    id: "administration.customization.subfield.create.modal.title",
    defaultMessage: "Criar novo subcampo",
  },
});

const SubfieldCreateModal: FC<SubfieldCreateModalProps> = ({
  disabledCodes,
  onCloseModal,
  onConfirm,
}) => {
  const { formatMessage } = useIntl();

  const firstEnabledCode = ALPHANUMERIC_CHARACTERS_VALUES.find(
    (value) => !disabledCodes.has(value),
  );

  if (firstEnabledCode === undefined) {
    throw new Error("All subfield codes are disabled");
  }

  const initialState: SubfieldFormEditorState = {
    code: firstEnabledCode,
    description: "",
    repeatable: false,
    collapsed: false,
    autocompleteType: "disabled",
    sortOrder: 0,
  };

  return (
    <SubfieldModal
      mode="create"
      disabledCodes={disabledCodes}
      title={formatMessage(messages.title)}
      subfieldFormEditorState={initialState}
      onCloseModal={onCloseModal}
      onConfirm={onConfirm}
    />
  );
};

export default SubfieldCreateModal;
