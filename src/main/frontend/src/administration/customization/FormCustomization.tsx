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
import React, { useState } from "react";

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
import { getAffectedItems } from "./lib";

const App: React.FC = () => {
  const { isLoading, isSuccess, data } = useFormDataQuery(RecordType.Biblio);

  if (isLoading) {
    return <EuiLoadingSpinner />;
  }

  return isSuccess && <DataFieldsDragAndDrop datafields={data} />;
};

type DataFieldsDragAndDropProps = {
  datafields: FormData[];
};

const DataFieldsDragAndDrop: FC<DataFieldsDragAndDropProps> = ({
  datafields,
}) => {
  const {
    data: translations,
    isLoading,
    isSuccess,
  } = useTranslationsQuery("pt-BR");

  const [items, setItems] = useState(datafields);

  const { mutate: saveFormDataFieldsMtn } = useSaveFormDataFieldsMutation();

  const { mutate: deleteFormDataFieldMtn } = useDeleteFormDataFieldMutation();

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

  return (
    isSuccess && (
      <EuiDragDropContext onDragEnd={onDragEnd}>
        <EuiDroppable
          droppableId="CUSTOM_HANDLE_DROPPABLE_AREA"
          spacing="m"
          withPanel
        >
          {items.map((item, idx) => (
            <EuiDraggable
              spacing="m"
              key={item.datafield}
              index={idx}
              draggableId={item.datafield}
              customDragHandle={true}
              hasInteractiveChildren={true}
            >
              {({ dragHandleProps }) => (
                <EuiPanel hasBorder={true} {...dragHandleProps}>
                  <EuiFlexGroup gutterSize="m">
                    <EuiFlexItem grow={false}>
                      <EuiFlexGroup alignItems="center">
                        <EuiIcon type="grab" />
                      </EuiFlexGroup>
                    </EuiFlexItem>
                    <EuiTitle size="xs">
                      <h2>
                        {`${item.datafield} - ${translations[`marc.bibliographic.datafield.${item.datafield}`]}`}
                      </h2>
                    </EuiTitle>
                    <EuiFlexGroup justifyContent="flexEnd" gutterSize="s">
                      <EuiButtonIcon
                        iconType="trash"
                        aria-label={`Apagar campo ${item.datafield}`}
                        onClick={() => {
                          setItems(
                            items.filter(
                              (_item) => _item.datafield !== item.datafield,
                            ),
                          );
                          deleteFormDataFieldMtn({
                            datafield: item.datafield,
                            recordType: RecordType.Biblio,
                          });
                        }}
                      />
                      <EuiButtonIcon
                        iconType="pencil"
                        aria-label={`Editar campo ${item.datafield}`}
                      />
                    </EuiFlexGroup>
                  </EuiFlexGroup>
                </EuiPanel>
              )}
            </EuiDraggable>
          ))}
        </EuiDroppable>
      </EuiDragDropContext>
    )
  );
};

export default App;
