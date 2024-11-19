import {
  EuiDragDropContext,
  EuiDraggable,
  EuiDroppable,
  EuiFlexGroup,
  EuiLoadingLogo,
  EuiImage,
  euiDragDropReorder,
  EuiButton,
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
import { getAffectedItems } from "./lib";
import EditDatafieldFlyout from "./EditDatafieldFlyout";
import type { FormFieldEditorState } from "./components/types";
import DatafieldPanel from "./components/DatafieldPanel";
import type { DatafieldTag } from "./types";
import { getTranslations } from "./components/translations_helpers";
import { FormattedMessage } from "react-intl";

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
): Omit<FormData, "sortOrder"> {
  return {
    datafield: formFieldEditorState.tag,
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

function getInitialEditorState(): FormFieldEditorState {
  return {
    tag: "",
    name: "",
    indicatorsState: [
      {
        defined: false,
        description: "",
        translations: {},
      },
      {
        defined: false,
        description: "",
        translations: {},
      },
    ],
    materialTypes: [],
    subfields: [],
    repeatable: false,
    collapsed: false,
  };
}

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

  const [datafieldToEdit, setDatafieldToEdit] = useState<FormData | undefined>(
    undefined,
  );

  const [creatingDatafield, setCreatingDatafield] = useState<boolean>(false);

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

  function handleDatafieldSave(formFieldEditorState: FormFieldEditorState) {
    if (datafields === undefined) {
      return; // should never happen
    }

    saveFormDatafieldsUsingEditorState({
      formFieldEditorState,
      recordType: "biblio",
      sortOrder: datafieldToEdit?.sortOrder ?? 0,
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

    setDatafieldToEdit(undefined);
  }

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

    setCreatingDatafield(false);
  }

  return (
    isSucessFormData &&
    isSuccessTranslations && (
      <Fragment>
        {creatingDatafield && (
          <EditDatafieldFlyout
            mode="create"
            editorState={getInitialEditorState()}
            onClose={() => setCreatingDatafield(false)}
            onSave={handleCreateFormData}
          />
        )}
        {translations && datafields && (
          <EuiFlexGroup direction="column">
            <DatafieldsDragAndDrop
              datafields={datafields}
              translations={translations}
              onDragEnd={onDragEnd}
              onDatafieldUpdate={handleDatafieldSave}
              onDataFieldDelete={handleFormDataDelete}
            />
            <EuiFlexGroup justifyContent="center">
              <EuiButton
                fill
                onClick={() => setCreatingDatafield(true)}
                iconType="plusInCircle"
              >
                <FormattedMessage
                  id="administration.customization.form.create"
                  defaultMessage="Criar campo"
                />
              </EuiButton>
            </EuiFlexGroup>
          </EuiFlexGroup>
        )}
      </Fragment>
    )
  );
};

type DatafieldsDragAndDropProps = {
  datafields: FormData[];
  translations: Record<string, string>;
  onDragEnd: OnDragEndResponder;
  onDatafieldUpdate: (datafield: FormFieldEditorState) => void;
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
                        onDatafieldUpdate={onDatafieldUpdate}
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
