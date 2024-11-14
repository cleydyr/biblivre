import {
  EuiButtonIcon,
  EuiDragDropContext,
  euiDragDropReorder,
  EuiDraggable,
  EuiDroppable,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFlyout,
  EuiFlyoutBody,
  EuiFlyoutHeader,
  EuiIcon,
  EuiLoadingSpinner,
  EuiPanel,
  EuiTitle,
  useGeneratedHtmlId,
  EuiFlyoutFooter,
  EuiButton,
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
import { FormattedMessage } from "react-intl";
import ConfirmDeleteModal from "./components/ConfirmDeleteModal";
import FormFieldEditor from "./FormFieldEditor";

const FormCustomization: React.FC = () => {
  const { isLoading, isSuccess, data } = useFormDataQuery(RecordType.Biblio);

  if (isLoading) {
    return <EuiLoadingSpinner />;
  }

  return isSuccess && <DataFieldsDragAndDrop datafields={data} />;
};

type DataFieldsDragAndDropProps = {
  datafields: FormData[];
};

const FormFieldTitle: FC<{
  tag: string;
  translations: Record<string, string>;
}> = ({ tag, translations }) => {
  return <>{`${tag} - ${getFieldNameTranslation(translations, tag)}`}</>;
};

const DataFieldsDragAndDrop: FC<DataFieldsDragAndDropProps> = ({
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

  const simpleFlyoutTitleId = useGeneratedHtmlId({
    prefix: "simpleFlyoutTitle",
  });

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

  return (
    <Fragment>
      {datafieldToEdit && (
        <EuiFlyout
          ownFocus
          onClose={() => setDatafieldToEdit(undefined)}
          aria-labelledby={simpleFlyoutTitleId}
        >
          <EuiFlyoutHeader>
            <EuiTitle size="m">
              <h2 id={simpleFlyoutTitleId}>
                <FormattedMessage
                  id="administration.customization.datafield.edit"
                  defaultMessage="Editar campo {tag}"
                  values={{
                    tag: datafieldToEdit.datafield,
                  }}
                />
              </h2>
            </EuiTitle>
          </EuiFlyoutHeader>
          <EuiFlyoutBody>
            <FormFieldEditor field={datafieldToEdit} onSave={() => {}} />
          </EuiFlyoutBody>
          <EuiFlyoutFooter>
            <EuiFlexGroup justifyContent="flexEnd">
              <EuiFlexItem grow={false}>
                <EuiButton
                  onClick={() => {
                    setDatafieldToEdit(undefined);
                  }}
                >
                  <FormattedMessage
                    id="administration.customization.form.cancel"
                    defaultMessage="Cancelar"
                  />
                </EuiButton>
              </EuiFlexItem>
              <EuiFlexItem grow={false}>
                <EuiButton
                  fill
                  onClick={() => {
                    saveFormDataFieldsMtn({
                      fields: [datafieldToEdit],
                      recordType: RecordType.Biblio,
                    });
                    setDatafieldToEdit(undefined);
                  }}
                >
                  <FormattedMessage
                    id="administration.customization.form.save"
                    defaultMessage="Salvar"
                  />
                </EuiButton>
              </EuiFlexItem>
            </EuiFlexGroup>
          </EuiFlyoutFooter>
        </EuiFlyout>
      )}
      {datafieldToDelete && (
        <ConfirmDeleteModal
          value={datafieldToDelete.datafield}
          onClose={() => {
            setDatafieldToDelete(undefined);
          }}
          onConfirm={() => {
            onFormDataDelete(datafieldToDelete);

            setDatafieldToDelete(undefined);
          }}
          modalBody={
            <FormattedMessage
              id="administration.customization.datafield.confirm_delete_message"
              defaultMessage="Esta operação é irreversível, e o campo só será apresentado na aba Marc.
          A informação a ser deletada só pode ser recriada manualmente."
            />
          }
          formRowLabel={
            <FormattedMessage
              id="administration.customization.datafield.confirm_delete_input"
              defaultMessage="Digite <strong>{tag}</strong> para habilitar o botão de confirmar a exclusão"
              values={{
                tag: datafieldToDelete.datafield,
                strong: (msg) => <strong>{msg}</strong>,
              }}
            />
          }
          title={
            <FormattedMessage
              id="administration.customization.datafield.confirm_delete"
              defaultMessage="Excluir campo {tag}?"
              values={{
                tag: datafieldToDelete.datafield,
              }}
            />
          }
          confirmButtonText={
            <FormattedMessage
              id="administration.customization.datafield.confirm_delete"
              defaultMessage="Entendo os riscos, excluir assim mesmo"
            />
          }
          cancelButtonText={
            <FormattedMessage
              id="administration.customization.datafield.cancel_delete"
              defaultMessage="Cancelar remoção"
            />
          }
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
