import type { ReactNode } from "react";
import { useState } from "react";
import { EuiConfirmModal, EuiFieldText, EuiFormRow } from "@elastic/eui";
import React from "react";
import { useToggle } from "@uidotdev/usehooks";
import type { EuiConfirmModalProps } from "@elastic/eui/src/components/modal/confirm_modal";

type ConfirmDeleteModalProps<T> = {
  value: T;
  onClose: () => void;
  onConfirm: () => void;
  modalBody: ReactNode;
  formRowLabel: ReactNode;
} & Pick<
  EuiConfirmModalProps,
  "title" | "cancelButtonText" | "confirmButtonText"
>;

function ConfirmDeleteModal<T>({
  value,
  title,
  onClose,
  onConfirm,
  modalBody,
  formRowLabel,
  confirmButtonText,
  cancelButtonText,
}: ConfirmDeleteModalProps<T>) {
  const [confirmationInputText, setConfirmationInputText] = useState("");

  const [isConfirmationInputValid, setIsConfirmationInputValid] =
    useToggle(false);

  return (
    <EuiConfirmModal
      title={title}
      onCancel={onClose}
      onConfirm={onConfirm}
      confirmButtonText={confirmButtonText}
      cancelButtonText={cancelButtonText}
      confirmButtonDisabled={!isConfirmationInputValid}
      buttonColor="danger"
    >
      <p>{modalBody}</p>
      <EuiFormRow label={formRowLabel}>
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
