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
import ConfirmDeleteModal from "./ConfirmDeleteModal";

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

  const [deletingIndicatorValue, setDeletingIndicatorValue] = useState<
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
          name: (
            <FormattedMessage
              id="administration.customization.indicator.action.edit"
              defaultMessage="Editar"
            />
          ),
          description: (item: IndicatorValueNameInfo) =>
            formatMessage(messages.editIndicatorValueDescription, {
              value: item.code,
              indicator: order + 1,
            }),
          onClick: (item) => setEditingIndicator(item),
        },
        {
          icon: "trash",
          type: "icon",
          name: (
            <FormattedMessage
              id="administration.customization.indicator.action.delete"
              defaultMessage="Remover"
            />
          ),
          description: (item: IndicatorValueNameInfo) =>
            formatMessage(messages.removeIndicatorValueDescription, {
              value: item.code,
              indicator: order + 1,
            }),
          onClick: setDeletingIndicatorValue,
        },
      ],
    },
  ];

  return (
    <Fragment>
      {deletingIndicatorValue && (
        <ConfirmDeleteModal
          title={
            <FormattedMessage
              id="administration.customization.indicator.confirm_delete"
              defaultMessage="Excluir informação do valor {code} do indicador {humanReadableOrder}?"
              values={{
                code: deletingIndicatorValue.code,
                humanReadableOrder: order + 1,
              }}
            />
          }
          value={deletingIndicatorValue.code}
          onClose={() => {
            setDeletingIndicatorValue(undefined);
          }}
          onConfirm={() => {
            handleDeleteIndicadorValue(deletingIndicatorValue);
            setDeletingIndicatorValue(undefined);
          }}
          modalBody={
            <FormattedMessage
              id="administration.customization.indicator.confirm_delete_message"
              defaultMessage="Esta operação é irreversível. A informação a ser deletada só pode ser recriada manualmente."
            />
          }
          confirmButtonText={
            <FormattedMessage
              id="administration.customization.indicator.confirm_delete"
              defaultMessage="Entendo os riscos, excluir assim mesmo"
            />
          }
          cancelButtonText={
            <FormattedMessage
              id="administration.customization.indicator.cancel_delete"
              defaultMessage="Cancelar remoção"
            />
          }
          formRowLabel={
            <FormattedMessage
              id="administration.customization.indicator.confirm_delete_input"
              defaultMessage="Digite <strong>{code}</strong> para habilitar o botão de confirmar a exclusão"
              values={{
                code: deletingIndicatorValue.code,
                strong: (msg) => <strong>{msg}</strong>,
              }}
            />
          }
        />
      )}
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
