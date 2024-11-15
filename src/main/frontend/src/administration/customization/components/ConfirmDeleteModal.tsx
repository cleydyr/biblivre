import type { ReactNode } from "react";
import { useState } from "react";
import { EuiConfirmModal, EuiFieldText, EuiFormRow } from "@elastic/eui";
import React from "react";
import { useToggle } from "@uidotdev/usehooks";
import type { EuiConfirmModalProps } from "@elastic/eui/src/components/modal/confirm_modal";
import { FormattedMessage } from "react-intl";

type ConfirmDeleteModalProps<T> = {
  value: T;
  onClose: () => void;
  onConfirm: () => void;
  modalBody: ReactNode;
} & Pick<EuiConfirmModalProps, "title">;

function ConfirmDeleteModal<T extends string>({
  value,
  title,
  onClose,
  onConfirm,
  modalBody,
}: ConfirmDeleteModalProps<T>) {
  const [confirmationInputText, setConfirmationInputText] = useState("");

  const [isConfirmationInputValid, setIsConfirmationInputValid] =
    useToggle(false);

  return (
    <EuiConfirmModal
      title={title}
      onCancel={onClose}
      onConfirm={onConfirm}
      cancelButtonText={
        <FormattedMessage
          id="administration.customization.subfield.cancel_delete"
          defaultMessage="Cancelar exclusão"
        />
      }
      confirmButtonText={
        <FormattedMessage
          id="administration.customization.subfield.confirm_delete"
          defaultMessage="Entendo os riscos, excluir assim mesmo"
        />
      }
      confirmButtonDisabled={!isConfirmationInputValid}
      buttonColor="danger"
    >
      <p>{modalBody}</p>
      <EuiFormRow
        label={
          <FormattedMessage
            id="administration.customization.subfield.confirm_delete_input"
            defaultMessage="Digite <strong>{code}</strong> para habilitar o botão de confirmar a exclusão"
            values={{
              code: value,
              strong: (msg) => <strong>{msg}</strong>,
            }}
          />
        }
      >
        <EuiFieldText
          value={confirmationInputText}
          onChange={(e) => {
            setConfirmationInputText(e.target.value);

            setIsConfirmationInputValid(e.target.value === value);
          }}
        />
      </EuiFormRow>
    </EuiConfirmModal>
  );
}

export default ConfirmDeleteModal;
