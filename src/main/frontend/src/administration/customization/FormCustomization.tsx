import {useQuery} from "@tanstack/react-query";

import {EuiTitle, EuiPanel, EuiLoadingSpinner} from "@elastic/eui";
import React from "react";

import "@elastic/eui/dist/eui_theme_light.css";
import {Configuration, FormDataApi, InitOverrideFunction, RecordType, ReportTemplateApi} from "../../generated-sources";
import {getSchemaFromURL} from "../../util";

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
        data.map((item) => (
            <EuiPanel hasBorder={true}>
                <EuiTitle size='xs'><h2>{item.datafield}</h2></EuiTitle>
            </EuiPanel>
        ));
};

function baseEndpointPath() {
    return `${window.location.origin}/api/v2`;
}

function useFormDataQuery(recordType: RecordType) {
    const apiConfiguration = new Configuration({
        basePath: baseEndpointPath(),
    });

    const api = new FormDataApi(apiConfiguration);

    return useQuery({
        queryKey: ['FORM_DATA'],
        queryFn: () => api.getFormData({
            recordType,
        }, DEFAULT_FETCH_OPTIONS),
    });
}

const DEFAULT_FETCH_OPTIONS: InitOverrideFunction = async ({init}) => ({
    ...init,
    headers: {
        ...init.headers,
        "X-Biblivre-Schema": getSchemaFromURL(),
        Accept: "application/json",
    },
});

export default App;
