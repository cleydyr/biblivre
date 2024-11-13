import { FormattedMessage } from "react-intl";
import type { FC } from "react";
import { useState } from "react";
import {
  EuiCode,
  EuiConfirmModal,
  EuiFieldText,
  EuiFormRow,
} from "@elastic/eui";
import React from "react";
import type { SubfieldCode } from "../types";
import { useToggle } from "@uidotdev/usehooks";

type SubfieldConfirmDeleteModalProps = {
  code: SubfieldCode;
  onClose: () => void;
  onConfirm: () => void;
};

const SubfieldConfirmDeleteModal: FC<SubfieldConfirmDeleteModalProps> = ({
  code,
  onClose,
  onConfirm,
}) => {
  const [confirmationInputText, setConfirmationInputText] = useState("");

  const [isConfirmationInputValid, setIsConfirmationInputValid] =
    useToggle(false);

  return (
    <EuiConfirmModal
      title={
        <FormattedMessage
          id="administration.customization.subfield.confirm_delete"
          defaultMessage="Excluir subcampo {code}?"
          values={{
            code,
          }}
        />
      }
      onCancel={onClose}
      onConfirm={onConfirm}
      cancelButtonText={
        <FormattedMessage
          id="administration.customization.subfield.cancel_delete"
          defaultMessage="Cancelar remoção"
        />
      }
      confirmButtonText={
        <FormattedMessage
          id="administration.customization.subfield.confirm_delete"
          defaultMessage="Entendo os riscos, excluir assim mesmo"
        />
      }
      confirmButtonDisabled={!isConfirmationInputValid}
    >
      <p>
        <FormattedMessage
          id="administration.customization.subfield.confirm_delete_message"
          defaultMessage="Esta operação é irreversível, e o campo só será apresentado na aba Marc.
          A informação a ser deletada só pode ser recriada manualmente."
          values={{ code, monospace: (msg) => <EuiCode>{msg}</EuiCode> }}
        />
      </p>
      <EuiFormRow
        label={
          <FormattedMessage
            id="administration.customization.subfield.confirm_delete_input"
            defaultMessage="Digite <strong>{code}</strong> para habilitar o botão de confirmar a exclusão"
            values={{ code, strong: (msg) => <strong>{msg}</strong> }}
          />
        }
      >
        <EuiFieldText
          value={confirmationInputText}
          onChange={(e) => {
            setConfirmationInputText(e.target.value);

            setIsConfirmationInputValid(e.target.value === code);
          }}
        />
      </EuiFormRow>
    </EuiConfirmModal>
  );
};

export default SubfieldConfirmDeleteModal;
