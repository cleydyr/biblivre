import {Configuration} from "../../generated-sources";
import {baseEndpointPath, DEFAULT_FETCH_OPTIONS} from "../../util";
import {useQuery, UseQueryOptions, UseQueryResult} from "@tanstack/react-query";

type ApiConstructor<T> = new (configuration: Configuration) => T;

type ApiFunction<TParams, TResponse> = (
    params: TParams,
    options: typeof DEFAULT_FETCH_OPTIONS
) => Promise<TResponse>;

function createApiInstance<T>(ApiClass: ApiConstructor<T>): T {
    const apiConfiguration = new Configuration({
        basePath: baseEndpointPath(),
    });

    return new ApiClass(apiConfiguration);
}

export function useGenericQuery<
    TApiClass,
    TParams,
    TResponse,
    TError = unknown
>(
    ApiClass: ApiConstructor<TApiClass>,
    queryKey: unknown[],
    apiFn: (api: TApiClass) => ApiFunction<TParams, TResponse>,
    params: TParams,
    options?: Omit<
        UseQueryOptions<TResponse, TError, TResponse>,
        'queryKey' | 'queryFn'
    >
): UseQueryResult<TResponse, TError> {
    return useQuery({
        queryKey,
        queryFn: () => {
            const api = createApiInstance(ApiClass);

            const boundFn = apiFn(api).bind(api);

            return boundFn(params, DEFAULT_FETCH_OPTIONS);
        },
        ...options,
    });
}
