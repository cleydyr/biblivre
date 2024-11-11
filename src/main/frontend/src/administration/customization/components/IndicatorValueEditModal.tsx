import type { FC } from "react";
import { useState } from "react";
import React from "react";
import {
  EuiButton,
  EuiButtonEmpty,
  EuiFieldText,
  EuiFlexGroup,
  EuiFlexItem,
  EuiForm,
  EuiFormRow,
  EuiModal,
  EuiModalBody,
  EuiModalFooter,
  EuiModalHeader,
  EuiModalHeaderTitle,
  EuiSelect,
  useGeneratedHtmlId,
} from "@elastic/eui";
import { FormattedMessage } from "react-intl";
import type { IndicatorCode } from "./types";
import { INDICATOR_CODE_VALUES } from "./types";
import { toIndicatorCode } from "../lib";

type IndicatorValueEditorModalProps = {
  indicatorCode: IndicatorCode;
  indicatorCodeTranslation: string;
  disabledCodes: Set<IndicatorCode>;
  onCloseModal: () => void;
  onConfirm: (
    indicatorCode: IndicatorCode,
    indicatorCodeTranslation: string,
  ) => void;
};

const IndicatorValueEditModal: FC<IndicatorValueEditorModalProps> = ({
  indicatorCode,
  indicatorCodeTranslation,
  onCloseModal,
  disabledCodes,
  onConfirm,
}) => {
  const [selectedIndicatorCode, setSelectedIndicatorCode] =
    useState(indicatorCode);

  const [editedDescription, setEditedDescription] = useState(
    indicatorCodeTranslation,
  );

  const modalTitleId = useGeneratedHtmlId();

  const modalFormId = useGeneratedHtmlId({ prefix: "modalForm" });

  return (
    <EuiModal
      aria-labelledby={modalTitleId}
      onClose={onCloseModal}
      initialFocus="[name=popswitch]"
    >
      <EuiModalHeader>
        <EuiModalHeaderTitle id={modalTitleId}>
          Editar valor do indicador
        </EuiModalHeaderTitle>
      </EuiModalHeader>

      <EuiModalBody>
        <EuiForm>
          <EuiFlexGroup>
            <EuiFlexItem grow={false}>
              <EuiFormRow
                label={
                  <FormattedMessage
                    id="administration.customization.indicator.code"
                    defaultMessage="Código"
                  />
                }
              >
                <EuiSelect
                  options={INDICATOR_CODE_VALUES.map((value) => ({
                    value,
                    text: value,
                    disabled: disabledCodes.has(value),
                  }))}
                  value={selectedIndicatorCode}
                  onChange={(event) =>
                    setSelectedIndicatorCode(
                      toIndicatorCode(event.target.value),
                    )
                  }
                />
              </EuiFormRow>
            </EuiFlexItem>
            <EuiFlexItem grow={false}>
              <EuiFormRow
                label={
                  <FormattedMessage
                    id="administration.customization.indicator.description"
                    defaultMessage="Descrição"
                  />
                }
              >
                <EuiFieldText
                  name="description"
                  value={editedDescription}
                  onChange={(event) => setEditedDescription(event.target.value)}
                ></EuiFieldText>
              </EuiFormRow>
            </EuiFlexItem>
          </EuiFlexGroup>
        </EuiForm>
      </EuiModalBody>

      <EuiModalFooter>
        <EuiButtonEmpty onClick={onCloseModal}>Cancel</EuiButtonEmpty>

        <EuiButton
          type="submit"
          form={modalFormId}
          onClick={() => {
            onConfirm(selectedIndicatorCode, editedDescription);

            onCloseModal();
          }}
          fill
        >
          Save
        </EuiButton>
      </EuiModalFooter>
    </EuiModal>
  );
};

export default IndicatorValueEditModal;
