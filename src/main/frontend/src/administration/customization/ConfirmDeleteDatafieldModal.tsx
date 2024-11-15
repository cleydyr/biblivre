import type { FC } from "react";
import React from "react";
import ConfirmDeleteModal from "./components/ConfirmDeleteModal";
import { FormattedMessage } from "react-intl";
import type { DatafieldTag } from "./types";

type ConfirmDeleteDataFieldModalProps = {
  tag: DatafieldTag;
  onConfirm: () => void;
  onClose: () => void;
};

const ConfirmDeleteDatafieldModal: FC<ConfirmDeleteDataFieldModalProps> = ({
  tag,
  onConfirm,
  onClose,
}) => {
  return (
    <ConfirmDeleteModal
      value={tag}
      onClose={onClose}
      onConfirm={onConfirm}
      modalBody={
        <FormattedMessage
          id="administration.customization.datafield.confirm_delete_message"
          defaultMessage="Esta operação é irreversível, e o campo só será apresentado na aba Marc.
          A informação a ser deletada só pode ser recriada manualmente."
        />
      }
      title={
        <FormattedMessage
          id="administration.customization.datafield.confirm_delete"
          defaultMessage="Excluir campo {tag}?"
          values={{
            tag,
          }}
        />
      }
    />
  );
};

export default ConfirmDeleteDatafieldModal;
