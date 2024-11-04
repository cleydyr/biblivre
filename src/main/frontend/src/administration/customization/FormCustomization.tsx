import {
    EuiTitle,
    EuiPanel,
    EuiLoadingSpinner,
    EuiDragDropContext,
    EuiDroppable,
    EuiDraggable,
    EuiIcon,
    EuiFlexGroup,
    EuiFlexItem,
    euiDragDropReorder
} from "@elastic/eui";
import React, {FC, useState} from "react";

import "@elastic/eui/dist/eui_theme_light.css";
import {RecordType} from "../../generated-sources";
import {useFormDataQuery, useTranslationQuery} from "./queries";
import {OnDragEndResponder} from "@hello-pangea/dnd";
import type {FormData} from "../../generated-sources";

const App: React.FC = () => {
    const {
        isLoading,
        isSuccess,
        data,
    } = useFormDataQuery(RecordType.Biblio);

    if (isLoading) {
        return <EuiLoadingSpinner/>
    }

    return isSuccess &&
        <DataFieldsDragAndDrop datafields={data}/>
};

type DataFieldsDragAndDropProps = {
    datafields: FormData[]
}

const DataFieldsDragAndDrop: FC<DataFieldsDragAndDropProps> = ({datafields}) => {
    const {
        data: translations,
        isLoading,
        isSuccess,
    } = useTranslationQuery('pt-BR')

    const [items, setItems] = useState(datafields)

    if (isLoading) {
        return <EuiLoadingSpinner/>
    }

    const onDragEnd: OnDragEndResponder = ({source, destination}) => {
        if (source && destination) {
            setItems(euiDragDropReorder(items, source.index, destination.index));
        }
    };

    return isSuccess && (<EuiDragDropContext onDragEnd={onDragEnd}>
        <EuiDroppable
            droppableId="CUSTOM_HANDLE_DROPPABLE_AREA"
            spacing="m"
            withPanel
        >
            {
                items.map(({datafield}, idx) => (
                    <EuiDraggable
                        spacing="m"
                        key={datafield}
                        index={idx}
                        draggableId={datafield}
                        customDragHandle={true}
                        hasInteractiveChildren={true}
                    >
                        {({dragHandleProps}) => (
                            <EuiPanel hasBorder={true} {...dragHandleProps} >
                                <EuiFlexGroup gutterSize='m'>
                                    <EuiFlexItem grow={false}>
                                        <EuiFlexGroup alignItems="center">
                                            <EuiIcon type="grab"/>
                                        </EuiFlexGroup>
                                    </EuiFlexItem>
                                    <EuiTitle size='xs'>
                                        <h2>{datafield} - {translations?.[`marc.bibliographic.datafield.${datafield}`]}</h2>
                                    </EuiTitle>
                                </EuiFlexGroup>
                            </EuiPanel>
                        )}
                    </EuiDraggable>
                ))
            }
        </EuiDroppable>
    </EuiDragDropContext>)
}

export default App;
