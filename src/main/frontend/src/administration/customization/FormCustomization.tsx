import {
  EuiDragDropContext,
  EuiDraggable,
  EuiDroppable,
  EuiFlexGroup,
  euiDragDropReorder,
} from "@elastic/eui";
import type { FC } from "react";
import React, { Fragment, useState, useEffect } from "react";
import type { OnDragEndResponder } from "@hello-pangea/dnd";
import type { FormData } from "../../generated-sources";
import { RecordType } from "../../generated-sources";
import {
  useDeleteFormDataFieldMutation,
  useFormDataQuery,
  useSaveFormDataFieldsMutation,
  useSaveFormDatafieldsUsingEditorStateMutation,
  useTranslationsQuery,
} from "./queries";
import { getAffectedItems, toPartialFormData } from "./lib";
import type { FormFieldEditorState } from "./components/types";
import DatafieldPanel from "./components/DatafieldPanel";
import type { DatafieldTag } from "./types";
import { getTranslations } from "./components/translations_helpers";
import BiblivreLoadingIcon from "./components/BiblivreLoadingIcon";
import CreateDatafieldAffordance from "./components/CreateDatafieldAffordance";

const FormCustomization: React.FC = () => {
  const {
    isLoading: isLoadingFormData,
    isSuccess: isSucessFormData,
    data: fetchedDatafields,
  } = useFormDataQuery(RecordType.Biblio);

  const {
    data: fetchedTranslations,
    isLoading: isLoadingTranslations,
    isSuccess: isSuccessTranslations,
  } = useTranslationsQuery("pt-BR");

  const [datafields, setDatafields] = useState<FormData[] | undefined>(
    fetchedDatafields,
  );

  const [translations, setTranslations] = useState<
    Record<string, string> | undefined
  >(fetchedTranslations);

  useEffect(() => {
    setTranslations(fetchedTranslations);
  }, [fetchedTranslations]);

  useEffect(() => {
    setDatafields(fetchedDatafields);
  }, [fetchedDatafields]);

  const { mutate: saveFormDatafieldsUsingEditorState } =
    useSaveFormDatafieldsUsingEditorStateMutation();

  const { mutate: saveFormDataFieldsReorderMtn } =
    useSaveFormDataFieldsMutation();

  const { mutate: deleteFormDataFieldMtn } = useDeleteFormDataFieldMutation();

  if (isLoadingFormData || isLoadingTranslations) {
    return <BiblivreLoadingIcon />;
  }

  const onDragEnd: OnDragEndResponder = ({ source, destination }) => {
    if (datafields === undefined) {
      return;
    }

    if (source && destination) {
      const reorderedItems = euiDragDropReorder(
        datafields,
        source.index,
        destination.index,
      );

      setDatafields(reorderedItems);

      saveFormDataFieldsReorderMtn({
        fields: getAffectedItems(datafields, source.index, destination.index),
        recordType: RecordType.Biblio,
      });
    }
  };

  const handleDatafieldSave =
    (sortOrder: number) => (formFieldEditorState: FormFieldEditorState) => {
      if (datafields === undefined) {
        return; // should never happen
      }

      saveFormDatafieldsUsingEditorState({
        formFieldEditorState,
        recordType: "biblio",
        sortOrder,
      });

      const updatedDatafields = datafields.map((datafield) =>
        datafield.datafield === formFieldEditorState.tag
          ? {
              ...datafield,
              ...toPartialFormData(formFieldEditorState),
            }
          : datafield,
      );

      setDatafields(updatedDatafields);

      const updateTranslations = {
        ...translations,
        ...getTranslations(formFieldEditorState),
      };

      setTranslations(updateTranslations);
    };

  function handleFormDataDelete(tag: DatafieldTag) {
    setDatafields(datafields?.filter((item) => item.datafield !== tag));

    deleteFormDataFieldMtn({
      datafield: tag,
      recordType: RecordType.Biblio,
    });
  }

  function handleCreateFormData(formFieldEditorState: FormFieldEditorState) {
    if (datafields === undefined) {
      return; // should never happen
    }

    const largestSortOrder = datafields.reduce(
      (acc, cur) => Math.max(acc, cur.sortOrder),
      0,
    );

    saveFormDatafieldsUsingEditorState({
      formFieldEditorState,
      recordType: "biblio",
      sortOrder: largestSortOrder,
    });

    const newFormData: FormData = {
      ...toPartialFormData(formFieldEditorState),
      sortOrder: largestSortOrder + 1,
    };

    setDatafields([...datafields, newFormData]);

    const updateTranslations = {
      ...translations,
      ...getTranslations(formFieldEditorState),
    };

    setTranslations(updateTranslations);
  }

  const appReady =
    isSucessFormData && isSuccessTranslations && translations && datafields;

  return (
    appReady && (
      <EuiFlexGroup direction="column">
        <DatafieldsDragAndDrop
          datafields={datafields}
          translations={translations}
          onDragEnd={onDragEnd}
          onDatafieldUpdate={handleDatafieldSave}
          onDataFieldDelete={handleFormDataDelete}
        />
        <EuiFlexGroup justifyContent="center">
          <CreateDatafieldAffordance onCreateFormData={handleCreateFormData} />
        </EuiFlexGroup>
      </EuiFlexGroup>
    )
  );
};

type DatafieldsDragAndDropProps = {
  datafields: FormData[];
  translations: Record<string, string>;
  onDragEnd: OnDragEndResponder;
  onDatafieldUpdate: (
    sortOrder: number,
  ) => (datafield: FormFieldEditorState) => void;
  onDataFieldDelete: (tag: DatafieldTag) => void;
};

const DatafieldsDragAndDrop: FC<DatafieldsDragAndDropProps> = ({
  datafields,
  translations,
  onDragEnd,
  onDatafieldUpdate,
  onDataFieldDelete,
}) => {
  return (
    <Fragment>
      <EuiDragDropContext onDragEnd={onDragEnd}>
        <EuiDroppable
          droppableId="CUSTOM_HANDLE_DROPPABLE_AREA"
          spacing="m"
          withPanel
        >
          <Fragment>
            {datafields.map((item, idx) => {
              const { datafield } = item;

              return (
                <EuiDraggable
                  spacing="m"
                  key={datafield}
                  index={idx}
                  draggableId={datafield}
                  customDragHandle={true}
                  hasInteractiveChildren={true}
                >
                  {({ dragHandleProps }) => {
                    return (
                      <DatafieldPanel
                        dragHandleProps={dragHandleProps}
                        datafield={item}
                        translations={translations}
                        onDatafieldUpdate={onDatafieldUpdate(item.sortOrder)}
                        onDatafieldDelete={onDataFieldDelete}
                      />
                    );
                  }}
                </EuiDraggable>
              );
            })}
          </Fragment>
        </EuiDroppable>
      </EuiDragDropContext>
    </Fragment>
  );
};

export default FormCustomization;
