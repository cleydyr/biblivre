import type { FC } from "react";
import React, { Fragment, useState } from "react";
import type { FormFieldEditorState } from "../queries";
import type { IndicatorCode, IndicatorOrder, WithOnChange } from "./types";
import { FormattedMessage, useIntl } from "react-intl";
import {
  EuiBasicTable,
  type EuiBasicTableColumn,
  EuiFieldText,
  EuiFormRow,
} from "@elastic/eui";
import messages from "../messages";
import IndicatorValueEditModal from "./IndicatorValueEditModal";
import { toIndicatorCode } from "../lib";

type IndicatorValueNameInfo = {
  order: IndicatorOrder;
  code: IndicatorCode;
  description: string;
};

type IndicatorFormSectionProps = FormFieldEditorState &
  WithOnChange & {
    order: IndicatorOrder;
  };

const IndicatorFormSection: FC<IndicatorFormSectionProps> = ({
  order,
  indicatorsState,
  onChange,
}) => {
  const { description, translations } = indicatorsState[order];

  const { formatMessage } = useIntl();

  const [editingIndicator, setEditingIndicator] = useState<
    IndicatorValueNameInfo | undefined
  >(undefined);

  const disabledCodes = new Set(Object.keys(translations).map(toIndicatorCode));

  const items: IndicatorValueNameInfo[] = Object.entries(translations).map(
    ([code, description]) => ({
      order,
      code: toIndicatorCode(code),
      description,
    }),
  );

  const closeModal = () => setEditingIndicator(undefined);

  const handleConfirmIndicatorValueEdit =
    (previousIndicatorCode: IndicatorCode) =>
    (indicatorCode: IndicatorCode, indicatorCodeTranslation: string) => {
      const newTranslations = {
        ...indicatorsState[order].translations,
      };

      delete newTranslations[previousIndicatorCode];

      newTranslations[indicatorCode] = indicatorCodeTranslation;

      const newState = {
        ...indicatorsState[order],
        translations: newTranslations,
      };

      onChange({
        indicatorsState:
          order === 0
            ? [newState, indicatorsState[1]]
            : [indicatorsState[0], newState],
      });
    };

  const handleDeleteIndicadorValue = (item: IndicatorValueNameInfo) => {
    const newState = {
      ...indicatorsState[order],
    };

    delete newState.translations[item.code];

    onChange({
      indicatorsState:
        order === 0
          ? [newState, indicatorsState[1]]
          : [indicatorsState[0], newState],
    });
  };

  const columns: Array<EuiBasicTableColumn<IndicatorValueNameInfo>> = [
    {
      field: "code",
      name: (
        <FormattedMessage
          id="administration.customization.indicator.code"
          defaultMessage="Código"
        />
      ),
    },
    {
      field: "description",
      name: (
        <FormattedMessage
          id="administration.customization.indicator.description"
          defaultMessage="Descrição"
        />
      ),
    },
    {
      name: (
        <FormattedMessage
          id="administration.customization.indicator.actions"
          defaultMessage="Ações"
        />
      ),
      actions: [
        {
          icon: "pencil",
          type: "icon",
          name: (item: IndicatorValueNameInfo) => (
            <FormattedMessage
              id="administration.customization.indicator.action.edit"
              defaultMessage="Editar"
              values={{
                value: item.code,
              }}
            />
          ),
          description: (item: IndicatorValueNameInfo) =>
            formatMessage(messages.editDescription, {
              value: item.code,
            }),
          onClick: (item) => setEditingIndicator(item),
        },
        {
          icon: "trash",
          type: "icon",
          name: (item: IndicatorValueNameInfo) => (
            <FormattedMessage
              id="administration.customization.indicator.action.delete"
              defaultMessage="Remover"
              values={{
                value: item.code,
              }}
            />
          ),
          description: (item: IndicatorValueNameInfo) =>
            formatMessage(messages.removeDescription, {
              value: item.code,
            }),
          onClick: handleDeleteIndicadorValue,
        },
      ],
    },
  ];

  return (
    <Fragment>
      {editingIndicator && (
        <IndicatorValueEditModal
          indicatorCode={editingIndicator.code}
          indicatorCodeTranslation={editingIndicator.description}
          disabledCodes={disabledCodes}
          onCloseModal={closeModal}
          onConfirm={handleConfirmIndicatorValueEdit(editingIndicator.code)}
        />
      )}
      <EuiFormRow
        label={
          <FormattedMessage
            id="administration.customization.indicator.1.name"
            defaultMessage="Nome do indicador 1"
          />
        }
      >
        <EuiFieldText name="indicator1_name" value={description}></EuiFieldText>
      </EuiFormRow>
      <EuiBasicTable<IndicatorValueNameInfo>
        tableCaption="Valores do indicador 1"
        items={items}
        rowHeader="code"
        columns={columns}
      />
    </Fragment>
  );
};

export default IndicatorFormSection;
