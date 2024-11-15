import {
  EuiButtonIcon,
  EuiDragDropContext,
  euiDragDropReorder,
  EuiDraggable,
  EuiDroppable,
  EuiFlexGroup,
  EuiFlexItem,
  EuiIcon,
  EuiLoadingSpinner,
  EuiPanel,
  EuiTitle,
} from "@elastic/eui";
import type { FC } from "react";
import React, { Fragment, useState } from "react";

import "@elastic/eui/dist/eui_theme_light.css";
import type { FormData } from "../../generated-sources";
import { RecordType } from "../../generated-sources";
import {
  useDeleteFormDataFieldMutation,
  useFormDataQuery,
  useSaveFormDataFieldsMutation,
  useTranslationsQuery,
} from "./queries";
import type { OnDragEndResponder } from "@hello-pangea/dnd";
import {
  getAffectedItems,
  getFieldNameTranslation,
  toDatafieldTag,
} from "./lib";
import EditDatafieldFlyout from "./EditDatafieldFlyout";
import ConfirmDeleteDatafieldModal from "./ConfirmDeleteDatafieldModal";

const FormCustomization: React.FC = () => {
  const { isLoading, isSuccess, data } = useFormDataQuery(RecordType.Biblio);

  if (isLoading) {
    return <EuiLoadingSpinner />;
  }

  return isSuccess && <DatafieldsDragAndDrop datafields={data} />;
};

type DatafieldsDragAndDropProps = {
  datafields: FormData[];
};

const FormFieldTitle: FC<{
  tag: string;
  translations: Record<string, string>;
}> = ({ tag, translations }) => {
  return <>{`${tag} - ${getFieldNameTranslation(translations, tag)}`}</>;
};

const DatafieldsDragAndDrop: FC<DatafieldsDragAndDropProps> = ({
  datafields,
}) => {
  const {
    data: translations,
    isLoading,
    isSuccess,
  } = useTranslationsQuery("pt-BR");

  const [items, setItems] = useState<FormData[]>(datafields);

  const { mutate: saveFormDataFieldsMtn } = useSaveFormDataFieldsMutation();

  const { mutate: deleteFormDataFieldMtn } = useDeleteFormDataFieldMutation();

  const [datafieldToDelete, setDatafieldToDelete] = useState<
    FormData | undefined
  >(undefined);

  const [datafieldToEdit, setDatafieldToEdit] = useState<FormData | undefined>(
    undefined,
  );

  if (isLoading) {
    return <EuiLoadingSpinner />;
  }

  const onDragEnd: OnDragEndResponder = ({ source, destination }) => {
    if (source && destination) {
      const reorderedItems = euiDragDropReorder(
        items,
        source.index,
        destination.index,
      );

      setItems(reorderedItems);

      saveFormDataFieldsMtn({
        fields: getAffectedItems(items, source.index, destination.index),
        recordType: RecordType.Biblio,
      });
    }
  };

  function onFormDataDelete({ datafield }: FormData) {
    setItems(items.filter((item) => item.datafield !== datafield));

    deleteFormDataFieldMtn({
      datafield: toDatafieldTag(datafield),
      recordType: RecordType.Biblio,
    });
  }

  function handleDatafieldSave() {}

  return (
    <Fragment>
      {datafieldToEdit && (
        <EditDatafieldFlyout
          datafield={datafieldToEdit}
          onClose={() => setDatafieldToEdit(undefined)}
          onSave={handleDatafieldSave}
        />
      )}
      {datafieldToDelete && (
        <ConfirmDeleteDatafieldModal
          datafieldToDelete={datafieldToDelete}
          onConfirm={() => {
            onFormDataDelete(datafieldToDelete);
            setDatafieldToDelete(undefined);
          }}
          onClose={() => setDatafieldToDelete(undefined)}
        />
      )}
      {isSuccess && (
        <EuiDragDropContext onDragEnd={onDragEnd}>
          <EuiDroppable
            droppableId="CUSTOM_HANDLE_DROPPABLE_AREA"
            spacing="m"
            withPanel
          >
            <Fragment>
              {items.map((item, idx) => (
                <EuiDraggable
                  spacing="m"
                  key={item.datafield}
                  index={idx}
                  draggableId={item.datafield}
                  customDragHandle={true}
                  hasInteractiveChildren={true}
                >
                  {({ dragHandleProps }) => {
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
                                tag={item.datafield}
                                translations={translations}
                              />
                            </h2>
                          </EuiTitle>
                          <EuiFlexGroup justifyContent="flexEnd" gutterSize="s">
                            <EuiButtonIcon
                              iconType="trash"
                              aria-label={`Apagar campo ${item.datafield}`}
                              onClick={() => setDatafieldToDelete(item)}
                            />
                            <EuiButtonIcon
                              iconType="pencil"
                              aria-label={`Editar campo ${item.datafield}`}
                              onClick={() => setDatafieldToEdit({ ...item })}
                            />
                          </EuiFlexGroup>
                        </EuiFlexGroup>
                      </EuiPanel>
                    );
                  }}
                </EuiDraggable>
              ))}
            </Fragment>
          </EuiDroppable>
        </EuiDragDropContext>
      )}
    </Fragment>
  );
};

export default FormCustomization;
