import type { FC } from "react";
import { Fragment } from "react";
import { useState } from "react";
import React from "react";
import type { FormFieldEditorState, SubfieldFormEditorState } from "../queries";
import type { WithOnChange } from "./types";
import { defineMessages, FormattedMessage, useIntl } from "react-intl";
import type { EuiBasicTableColumn } from "@elastic/eui";
import { EuiBasicTable, EuiCode } from "@elastic/eui";
import type { AutocompleteType } from "../../../generated-sources";
import { autocompleteTypeMessageDescriptors } from "../messages";
import SubfieldEditModal from "./SubfieldEditModal";
import SubfieldConfirmDeleteModal from "./SubfieldConfirmDeleteModal";

const messages = defineMessages({
  editSubfieldValueDescription: {
    id: "administration.customization.subfield.action.edit.description",
    defaultMessage: "Editar valor do subcampo {value}",
  },
  removeSubfieldValueDescription: {
    id: "administration.customization.subfield.action.remove.description",
    defaultMessage: "Remover valor do subcampo {value}",
  },
  deleteSubfieldValueModalTitle: {
    id: "administration.customization.subfield.action.delete.title",
    defaultMessage: "Remover valor do subcampo {value}",
  },
  deleteSubfieldValueModalBody: {
    id: "administration.customization.subfield.action.delete.body",
    defaultMessage:
      "Tem certeza que deseja remover o valor do subcampo {value}? Esta operação é irreversível, e o campo só será apresentado na aba Marc.",
  },
});
const SubfieldSection: FC<FormFieldEditorState & WithOnChange> = ({
  subfields,
  onChange,
}) => {
  const { formatMessage } = useIntl();

  const [editingSubfield, setEditingSubfield] = useState<
    SubfieldFormEditorState | undefined
  >(undefined);

  const [deletingSubfield, setDeletingSubfield] = useState<
    SubfieldFormEditorState | undefined
  >(undefined);

  const handleDeleteSubfield = (
    subfieldFormEditorState: SubfieldFormEditorState,
  ) => {
    onChange({
      subfields: subfields.filter(
        (subfield) => subfieldFormEditorState.code !== subfield.code,
      ),
    });
  };

  const columns: Array<EuiBasicTableColumn<SubfieldFormEditorState>> = [
    {
      field: "code",
      name: (
        <FormattedMessage
          id="administration.customization.subfield.code"
          defaultMessage="Código"
        />
      ),
      render: (code: string) => <EuiCode>{code}</EuiCode>,
    },
    {
      field: "description",
      name: (
        <FormattedMessage
          id="administration.customization.subfield.description"
          defaultMessage="Descrição"
        />
      ),
    },
    {
      field: "repeatable",
      name: (
        <FormattedMessage
          id="administration.customization.subfield.repeatable"
          defaultMessage="Repetível"
        />
      ),
      render: yesOrNoMessage,
    },
    {
      field: "collapsed",
      name: (
        <FormattedMessage
          id="administration.customization.subfield.collapsed"
          defaultMessage="Recolhido"
        />
      ),
      render: yesOrNoMessage,
    },
    {
      field: "autocompleteType",
      name: (
        <FormattedMessage
          id="administration.customization.subfield.autocomplete_type"
          defaultMessage="Tipo de auto-completar"
        />
      ),
      render: (autoCompleteType: AutocompleteType) =>
        formatMessage(autocompleteTypeMessageDescriptors[autoCompleteType]),
    },
    {
      name: (
        <FormattedMessage
          id="administration.customization.subfield.actions"
          defaultMessage="Ações"
        />
      ),
      actions: [
        {
          icon: "pencil",
          type: "icon",
          name: (
            <FormattedMessage
              id="administration.customization.subfield.action.edit"
              defaultMessage="Editar"
            />
          ),
          description: (item: SubfieldFormEditorState) =>
            formatMessage(messages.editSubfieldValueDescription, {
              value: item.code,
            }),
          onClick: (item) => setEditingSubfield(item),
        },
        {
          icon: "trash",
          type: "icon",
          name: (
            <FormattedMessage
              id="administration.customization.subfield.action.delete"
              defaultMessage="Remover"
            />
          ),
          description: (item: SubfieldFormEditorState) =>
            formatMessage(messages.removeSubfieldValueDescription, {
              value: item.code,
            }),
          onClick: setDeletingSubfield,
        },
      ],
    },
  ];

  const handleSaveSubfield =
    (currentSubfieldFormState: SubfieldFormEditorState) =>
    (newSubfieldFormEditorState: SubfieldFormEditorState) => {
      onChange({
        subfields: subfields.map((subfield) => {
          if (subfield.code === currentSubfieldFormState.code) {
            return newSubfieldFormEditorState;
          }

          return subfield;
        }),
      });

      setEditingSubfield(undefined);
    };

  const disabledCodes = new Set(subfields.map((subfield) => subfield.code));

  return (
    <Fragment>
      {deletingSubfield && (
        <SubfieldConfirmDeleteModal
          code={deletingSubfield.code}
          onConfirm={() => {
            handleDeleteSubfield(deletingSubfield);
            setDeletingSubfield(undefined);
          }}
          onClose={() => setDeletingSubfield(undefined)}
        />
      )}
      {editingSubfield && (
        <SubfieldEditModal
          subfieldFormEditorState={editingSubfield}
          disabledCodes={disabledCodes}
          onConfirm={handleSaveSubfield(editingSubfield)}
          onCloseModal={() => setEditingSubfield(undefined)}
        />
      )}
      <EuiBasicTable<SubfieldFormEditorState>
        tableCaption="Subcampos"
        items={subfields}
        rowHeader="code"
        columns={columns}
      />
    </Fragment>
  );
};

function yesOrNoMessage(value: boolean) {
  return value ? (
    <FormattedMessage id="yes" defaultMessage="Sim" />
  ) : (
    <FormattedMessage id="no" defaultMessage="Não" />
  );
}

export default SubfieldSection;
