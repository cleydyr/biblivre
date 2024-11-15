import {
  EuiDragDropContext,
  EuiDraggable,
  EuiDroppable,
  EuiFlexGroup,
  EuiLoadingLogo,
  EuiImage,
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
import { getAffectedItems, toDatafieldTag } from "./lib";
import EditDatafieldFlyout from "./EditDatafieldFlyout";
import ConfirmDeleteDatafieldModal from "./ConfirmDeleteDatafieldModal";
import { toFormFieldEditorState } from "./components/lib";
import type { FormFieldEditorState } from "./components/types";
import DatafieldPanel from "./components/DatafieldPanel";
import type { DatafieldTag } from "./types";
import { getTranslations } from "./components/translations_helpers";

function BiblivreLoadingIcon() {
  return (
    <EuiFlexGroup justifyContent="center" alignItems="center">
      <EuiLoadingLogo
        size="xl"
        logo={() => (
          <EuiImage
            size="original"
            src="/static/images/logo_biblivre_small_original.png"
            alt="Bredo logo"
          />
        )}
      />
    </EuiFlexGroup>
  );
}

function toPartialFormData(
  formFieldEditorState: FormFieldEditorState,
): Partial<FormData> {
  return {
    indicator1: formFieldEditorState.indicatorsState[0].defined
      ? Object.keys(formFieldEditorState.indicatorsState[0].translations)
      : [],
    indicator2: formFieldEditorState.indicatorsState[1].defined
      ? Object.keys(formFieldEditorState.indicatorsState[1].translations)
      : [],
    materialType: formFieldEditorState.materialTypes,
    subfields: formFieldEditorState.subfields.map(
      ({ code, sortOrder, autocompleteType, repeatable, collapsed }) => ({
        datafield: formFieldEditorState.tag,
        subfield: code,
        sortOrder,
        autocompleteType,
        repeatable,
        collapsed,
      }),
    ),
    repeatable: formFieldEditorState.repeatable,
    collapsed: formFieldEditorState.collapsed,
  };
}

const FormCustomization: React.FC = () => {
  const {
    isLoading: isLoadingFormData,
    isSuccess: isSucessFormData,
    data: datafields,
  } = useFormDataQuery(RecordType.Biblio);

  const {
    data: fetchedTranslations,
    isLoading: isLoadingTranslations,
    isSuccess: isSuccessTranslations,
  } = useTranslationsQuery("pt-BR");

  const [items, setItems] = useState<FormData[] | undefined>(datafields);

  const [translations, setTranslations] = useState<
    Record<string, string> | undefined
  >(fetchedTranslations);

  useEffect(() => {
    setTranslations(fetchedTranslations);
  }, [fetchedTranslations]);

  useEffect(() => {
    setItems(datafields);
  }, [datafields]);

  const { mutate: saveFormDatafieldsUsingEditorState } =
    useSaveFormDatafieldsUsingEditorStateMutation();

  const { mutate: saveFormDataFieldsReorderMtn } =
    useSaveFormDataFieldsMutation();

  const { mutate: deleteFormDataFieldMtn } = useDeleteFormDataFieldMutation();

  const [datafieldToEdit, setDatafieldToEdit] = useState<FormData | undefined>(
    undefined,
  );

  const [datafieldToDelete, setDatafieldToDelete] = useState<
    DatafieldTag | undefined
  >(undefined);

  if (isLoadingFormData || isLoadingTranslations) {
    return <BiblivreLoadingIcon />;
  }

  const onDragEnd: OnDragEndResponder = ({ source, destination }) => {
    if (items === undefined) {
      return;
    }

    if (source && destination) {
      const reorderedItems = euiDragDropReorder(
        items,
        source.index,
        destination.index,
      );

      setItems(reorderedItems);

      saveFormDataFieldsReorderMtn({
        fields: getAffectedItems(items, source.index, destination.index),
        recordType: RecordType.Biblio,
      });
    }
  };

  function handleDatafieldSave(formFieldEditorState: FormFieldEditorState) {
    saveFormDatafieldsUsingEditorState({
      formFieldEditorState,
      recordType: "biblio",
    });

    const updatedItems = items?.map((item) =>
      item.datafield === formFieldEditorState.tag
        ? {
            ...item,
            ...toPartialFormData(formFieldEditorState),
          }
        : item,
    );

    setItems(updatedItems);

    setDatafieldToEdit(undefined);

    setTranslations({
      ...translations,
      ...getTranslations(formFieldEditorState),
    });
  }

  function handleFormDataDelete(tag: DatafieldTag) {
    setItems(items?.filter((item) => item.datafield !== tag));

    deleteFormDataFieldMtn({
      datafield: tag,
      recordType: RecordType.Biblio,
    });
  }

  function handleFormDataEdit(tag: DatafieldTag) {
    setDatafieldToEdit(items?.find((item) => item.datafield === tag));
  }

  return (
    isSucessFormData &&
    isSuccessTranslations && (
      <Fragment>
        {datafieldToEdit && translations && (
          <EditDatafieldFlyout
            mode={"edit"}
            editorState={toFormFieldEditorState(translations, datafieldToEdit)}
            onClose={() => setDatafieldToEdit(undefined)}
            onSave={handleDatafieldSave}
          />
        )}
        {datafieldToDelete && (
          <ConfirmDeleteDatafieldModal
            tag={datafieldToDelete}
            onConfirm={() => {
              handleFormDataDelete(datafieldToDelete);
              setDatafieldToDelete(undefined);
            }}
            onClose={() => setDatafieldToDelete(undefined)}
          />
        )}
        {translations && (
          <DatafieldsDragAndDrop
            datafields={datafields}
            translations={translations}
            onClickDeleteDatafield={setDatafieldToDelete}
            onClickEditDatafield={handleFormDataEdit}
            onDragEnd={onDragEnd}
          />
        )}
      </Fragment>
    )
  );
};

type DatafieldsDragAndDropProps = {
  datafields: FormData[];
  translations: Record<string, string>;
  onDragEnd: OnDragEndResponder;
  onClickDeleteDatafield: (tag: DatafieldTag) => void;
  onClickEditDatafield: (tag: DatafieldTag) => void;
};

const DatafieldsDragAndDrop: FC<DatafieldsDragAndDropProps> = ({
  datafields,
  translations,
  onDragEnd,
  onClickDeleteDatafield,
  onClickEditDatafield,
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
                        tag={toDatafieldTag(datafield)}
                        translations={translations}
                        // onClickDelete={() => setDatafieldToDelete(item)}
                        // onClickEdit={() => setDatafieldToEdit({ ...item })}
                        onClickDelete={onClickDeleteDatafield}
                        onClickEdit={onClickEditDatafield}
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
