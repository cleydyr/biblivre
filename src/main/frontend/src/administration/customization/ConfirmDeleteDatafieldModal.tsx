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
      formRowLabel={
        <FormattedMessage
          id="administration.customization.datafield.confirm_delete_input"
          defaultMessage="Digite <strong>{tag}</strong> para habilitar o botão de confirmar a exclusão"
          values={{
            tag: datafieldToDelete.datafield,
            strong: (msg) => <strong>{msg}</strong>,
          }}
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
      confirmButtonText={
        <FormattedMessage
          id="administration.customization.datafield.confirm_delete"
          defaultMessage="Entendo os riscos, excluir assim mesmo"
        />
      }
      cancelButtonText={
        <FormattedMessage
          id="administration.customization.datafield.cancel_delete"
          defaultMessage="Cancelar remoção"
        />
      }
    />
  );
};

export default ConfirmDeleteDatafieldModal;
