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
import { useToggle } from "@uidotdev/usehooks";
import type { IndicatorCode, IndicatorOrder } from "./types";
import { toIndicatorCode } from "../lib";

type IndicatorConfirmDeleteModalProps = {
  order: IndicatorOrder;
  code: IndicatorCode;
  onClose: () => void;
  onConfirm: () => void;
};

const IndicatorConfirmDeleteModal: FC<IndicatorConfirmDeleteModalProps> = ({
  order,
  code,
  onClose,
  onConfirm,
}) => {
  const [confirmationInputText, setConfirmationInputText] = useState("");

  const [isConfirmationInputValid, setIsConfirmationInputValid] =
    useToggle(false);

  const humanReadableOrder = order + 1;

  return (
    <EuiConfirmModal
      title={
        <FormattedMessage
          id="administration.customization.indicator.confirm_delete"
          defaultMessage="Excluir informação do valor {code} do indicador {humanReadableOrder}?"
          values={{
            code,
            humanReadableOrder,
          }}
        />
      }
      onCancel={onClose}
      onConfirm={onConfirm}
      cancelButtonText={
        <FormattedMessage
          id="administration.customization.indicator.cancel_delete"
          defaultMessage="Cancelar remoção"
        />
      }
      confirmButtonText={
        <FormattedMessage
          id="administration.customization.indicator.confirm_delete"
          defaultMessage="Entendo os riscos, excluir assim mesmo"
        />
      }
      confirmButtonDisabled={!isConfirmationInputValid}
    >
      <p>
        <FormattedMessage
          id="administration.customization.indicator.confirm_delete_message"
          defaultMessage="Esta operação é irreversível. A informação a ser deletada só pode ser recriada manualmente."
          values={{ code, monospace: (msg) => <EuiCode>{msg}</EuiCode> }}
        />
      </p>
      <EuiFormRow
        label={
          <FormattedMessage
            id="administration.customization.indicator.confirm_delete_input"
            defaultMessage="Digite <strong>{code}</strong> para habilitar o botão de confirmar a exclusão"
            values={{ code, strong: (msg) => <strong>{msg}</strong> }}
          />
        }
      >
        <EuiFieldText
          value={confirmationInputText}
          onChange={(e) => {
            setConfirmationInputText(e.target.value);

            setIsConfirmationInputValid(
              toIndicatorCode(e.target.value) === code,
            );
          }}
        />
      </EuiFormRow>
    </EuiConfirmModal>
  );
};

export default IndicatorConfirmDeleteModal;
