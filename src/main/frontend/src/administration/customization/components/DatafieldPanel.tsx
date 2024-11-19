import type { DraggableProvidedDragHandleProps } from "@elastic/eui";
import {
  EuiButtonIcon,
  EuiFlexGroup,
  EuiFlexItem,
  EuiIcon,
  EuiPanel,
  EuiTitle,
} from "@elastic/eui";
import type { FC } from "react";
import { Fragment, useState } from "react";
import React from "react";
import FormFieldTitle from "./FormFieldTitle";
import { toDatafieldTag } from "../lib";
import type { DatafieldTag } from "../types";
import { defineMessages, useIntl } from "react-intl";
import EditDatafieldFlyout from "../EditDatafieldFlyout";
import { toFormFieldEditorState } from "./lib";
import type { FormData } from "../../../generated-sources";
import type { FormFieldEditorState } from "./types";
import ConfirmDeleteDatafieldModal from "../ConfirmDeleteDatafieldModal";

const messages = defineMessages({
  delete: {
    id: "administration.customization.datafield.delete",
    defaultMessage: "Apagar campo {tag}",
  },
  edit: {
    id: "administration.customization.datafield.edit",
    defaultMessage: "Editar campo {tag}",
  },
});

type DatafieldPanelProps = {
  datafield: FormData;
  dragHandleProps: DraggableProvidedDragHandleProps | null;
  translations: Record<string, string>;
  onDatafieldUpdate: (datafield: FormFieldEditorState) => void;
  onDatafieldDelete: (tag: DatafieldTag) => void;
};

const DatafieldPanel: FC<DatafieldPanelProps> = ({
  datafield,
  translations,
  dragHandleProps,
  onDatafieldUpdate,
  onDatafieldDelete,
}) => {
  const { formatMessage } = useIntl();

  const [editing, setEditing] = useState(false);

  const [deleting, setDeleting] = useState(false);

  const { datafield: tag } = datafield;

  return (
    <Fragment>
      {deleting && (
        <ConfirmDeleteDatafieldModal
          tag={toDatafieldTag(tag)}
          onConfirm={() => {
            onDatafieldDelete(toDatafieldTag(tag));

            setDeleting(false);
          }}
          onClose={() => setDeleting(false)}
        />
      )}
      {editing && (
        <EditDatafieldFlyout
          mode={"edit"}
          editorState={toFormFieldEditorState(translations, datafield)}
          onClose={() => setEditing(false)}
          onSave={onDatafieldUpdate}
        />
      )}
      <EuiPanel hasBorder={true} {...dragHandleProps}>
        <EuiFlexGroup gutterSize="m">
          <EuiFlexItem grow={false}>
            <EuiFlexGroup alignItems="center">
              <EuiIcon type="grab" />
            </EuiFlexGroup>
          </EuiFlexItem>
          <EuiTitle size="xs">
            <h2>
              <FormFieldTitle
                tag={toDatafieldTag(tag)}
                translations={translations}
              />
            </h2>
          </EuiTitle>
          <EuiFlexGroup justifyContent="flexEnd" gutterSize="s">
            <EuiButtonIcon
              iconType="trash"
              aria-label={formatMessage(messages.delete, { tag })}
              onClick={() => setDeleting(true)}
            />
            <EuiButtonIcon
              iconType="pencil"
              aria-label={formatMessage(messages.edit, { tag })}
              onClick={() => setEditing(true)}
            />
          </EuiFlexGroup>
        </EuiFlexGroup>
      </EuiPanel>
    </Fragment>
  );
};

export default DatafieldPanel;
