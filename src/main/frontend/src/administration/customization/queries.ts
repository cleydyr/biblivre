import { DefaultApi, FormDataApi, RecordType} from "../../generated-sources";
import {useGenericQuery} from "../../generic";

export function useTranslationQuery(language: string) {
    return useGenericQuery(
        DefaultApi,
        ['TRANSLATIONS', language],
        (api) => api.getTranslations,
        [{ language }]
    );
}

export function useFormDataQuery(recordType: RecordType) {
    return useGenericQuery(
        FormDataApi,
        ['FORM_DATA', recordType],
        (api) => api.getFormData,
        [{ recordType }]
    );
}
