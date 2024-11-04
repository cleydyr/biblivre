import {EuiTitle, EuiPanel, EuiLoadingSpinner} from "@elastic/eui";
import React from "react";

import "@elastic/eui/dist/eui_theme_light.css";
import { RecordType} from "../../generated-sources";
import {useFormDataQuery, useTranslationQuery} from "./queries";

const App: React.FC = () => {
    const {
        isLoading: isFormDataLoading,
        isSuccess: isFormDataSuccess,
        data,
    } = useFormDataQuery(RecordType.Biblio);

    const {
        data: translations,
        isLoading: isTranslationsLoading,
        isSuccess: isTranslationsSuccess,
    } = useTranslationQuery('pt-BR')

    if (isFormDataLoading || isTranslationsLoading) {
        return <EuiLoadingSpinner/>
    }

    return isTranslationsSuccess && isFormDataSuccess &&
        data.map(({datafield}) => (
            <EuiPanel key={datafield} hasBorder={true}>
                <EuiTitle size='xs'><h2>{datafield} - {translations?.[`marc.bibliographic.datafield.${datafield}`]}</h2></EuiTitle>
            </EuiPanel>
        ));
};

export default App;
