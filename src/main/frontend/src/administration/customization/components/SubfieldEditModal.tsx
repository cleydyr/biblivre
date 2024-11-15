import type { FC } from "react";
import React from "react";

import { defineMessages, useIntl } from "react-intl";
import type { SubfieldFormEditorState } from "./types";
import SubfieldModal from "./SubfieldModal";

type SubfieldEditModalProps = {
  subfieldFormEditorState: SubfieldFormEditorState;
  onCloseModal: () => void;
  onConfirm: (subfield: SubfieldFormEditorState) => void;
};

const messages = defineMessages({
  title: {
    id: "administration.customization.subfield.edit.modal.title",
    defaultMessage: "Editar valor do subcampo ${code}",
  },
});

const SubfieldEditModal: FC<SubfieldEditModalProps> = ({
  subfieldFormEditorState,
  onCloseModal,
  onConfirm,
}) => {
  const { formatMessage } = useIntl();

  return (
    <SubfieldModal
      title={formatMessage(messages.title, {
        code: subfieldFormEditorState.code,
      })}
      subfieldFormEditorState={subfieldFormEditorState}
      onCloseModal={onCloseModal}
      onConfirm={onConfirm}
      mode="edit"
    />
  );
};

export default SubfieldEditModal;
