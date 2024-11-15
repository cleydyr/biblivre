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
            aria-label={`Apagar campo ${tag}`}
            onClick={() => onClickDelete(tag)}
          />
          <EuiButtonIcon
            iconType="pencil"
            aria-label={`Editar campo ${tag}`}
            onClick={() => onClickEdit(tag)}
          />
        </EuiFlexGroup>
      </EuiFlexGroup>
    </EuiPanel>
  );
};

export default DatafieldPanel;
