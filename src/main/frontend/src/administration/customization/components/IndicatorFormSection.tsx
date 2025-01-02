import type { FC } from "react";
import React, { Fragment, useState } from "react";
import type {
  FormFieldEditorState,
  IndicatorCode,
  IndicatorOrder,
  WithOnChange,
} from "./types";
import { INDICATOR_CODE_VALUES } from "./types";
import { FormattedMessage, useIntl } from "react-intl";
import {
  EuiBasicTable,
  type EuiBasicTableColumn,
  EuiButtonEmpty,
  EuiFieldText,
  EuiFlexGroup,
  EuiFormRow,
  EuiIcon,
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

  const [editingIndicatorDescription, setEditingIndicatorDescription] =
    useState<string>(description);

  const [creatingIndicator, setCreatingIndicator] = useState<boolean>(false);

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
          name: formatMessage(messages.edit),
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
          name: formatMessage(messages.delete),
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
          value={deletingIndicatorValue.code.toString()}
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
      {creatingIndicator && (
        <IndicatorValueEditModal
          indicatorCode={
            INDICATOR_CODE_VALUES.find((value) => !disabledCodes.has(value)) ??
            0
          }
          indicatorCodeTranslation=""
          disabledCodes={disabledCodes}
          onCloseModal={() => setCreatingIndicator(false)}
          onConfirm={(code, description) => {
            const newTranslations = {
              ...indicatorsState[order].translations,
            };

            newTranslations[code] = description;

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

            setCreatingIndicator(false);
          }}
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
        <EuiFieldText
          name="indicator1_name"
          value={editingIndicatorDescription}
          onChange={(event) => {
            setEditingIndicatorDescription(event.target.value);

            const newIndicatorState = {
              ...indicatorsState[order],
              description: event.target.value,
            };

            onChange({
              indicatorsState:
                order === 0
                  ? [newIndicatorState, indicatorsState[1]]
                  : [indicatorsState[0], newIndicatorState],
            });
          }}
        ></EuiFieldText>
      </EuiFormRow>
      <EuiBasicTable<IndicatorValueNameInfo>
        items={items}
        rowHeader="code"
        columns={columns}
      />
      <EuiFlexGroup justifyContent={"flexEnd"}>
        <EuiButtonEmpty onClick={() => setCreatingIndicator(true)}>
          <p>
            <EuiIcon type="plus" className="eui-alignMiddle" />
            <FormattedMessage
              id="administration.customization.indicator.add_value"
              defaultMessage="Adicionar valor"
            />
          </p>
        </EuiButtonEmpty>
      </EuiFlexGroup>
    </Fragment>
  );
};

export default IndicatorFormSection;
