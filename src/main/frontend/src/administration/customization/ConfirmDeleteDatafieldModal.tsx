import type { FormData } from "../../generated-sources";
import type { FC } from "react";
import React from "react";
import ConfirmDeleteModal from "./components/ConfirmDeleteModal";
import { FormattedMessage } from "react-intl";

type ConfirmDeleteDataFieldModalProps = {
  datafieldToDelete: FormData;
  onConfirm: () => void;
  onClose: () => void;
};

const ConfirmDeleteDatafieldModal: FC<ConfirmDeleteDataFieldModalProps> = ({
  datafieldToDelete,
  onConfirm,
  onClose,
}) => {
  return (
    <ConfirmDeleteModal
      value={datafieldToDelete.datafield}
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
            tag: datafieldToDelete.datafield,
          }}
        />
      }
    />
  );
};

export default ConfirmDeleteDatafieldModal;
