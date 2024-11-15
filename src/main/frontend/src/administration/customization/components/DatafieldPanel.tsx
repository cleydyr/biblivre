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
import React from "react";
import FormFieldTitle from "./FormFieldTitle";
import { toDatafieldTag } from "../lib";
import type { DatafieldTag } from "../types";
import { defineMessages, useIntl } from "react-intl";

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
  tag: DatafieldTag;
  dragHandleProps: DraggableProvidedDragHandleProps | null;
  translations: Record<string, string>;
  onClickDelete: (tag: DatafieldTag) => void;
  onClickEdit: (tag: DatafieldTag) => void;
};

const DatafieldPanel: FC<DatafieldPanelProps> = ({
  tag,
  translations,
  dragHandleProps,
  onClickDelete,
  onClickEdit,
}) => {
  const { formatMessage } = useIntl();

  return (
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
            onClick={() => onClickDelete(tag)}
          />
          <EuiButtonIcon
            iconType="pencil"
            aria-label={formatMessage(messages.edit, { tag })}
            onClick={() => onClickEdit(tag)}
          />
        </EuiFlexGroup>
      </EuiFlexGroup>
    </EuiPanel>
  );
};

export default DatafieldPanel;
