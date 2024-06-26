/// <reference path="./custom.d.ts" />
// tslint:disable
/**
 * Biblivre REST API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 0.0.1
 * 
 *
 * NOTE: This file is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the file manually.
 */

import * as url from "url";
import * as isomorphicFetch from "isomorphic-fetch";
import { Configuration } from "./configuration";

const BASE_PATH = "/".replace(/\/+$/, "");

/**
 *
 * @export
 */
export const COLLECTION_FORMATS = {
    csv: ",",
    ssv: " ",
    tsv: "\t",
    pipes: "|",
};

/**
 *
 * @export
 * @interface FetchAPI
 */
export interface FetchAPI {
    (url: string, init?: any): Promise<Response>;
}

/**
 *
 * @export
 * @interface FetchArgs
 */
export interface FetchArgs {
    url: string;
    options: any;
}

/**
 *
 * @export
 * @class BaseAPI
 */
export class BaseAPI {
    protected configuration: Configuration;

    constructor(configuration?: Configuration, protected basePath: string = BASE_PATH, protected fetch: FetchAPI = isomorphicFetch) {
        if (configuration) {
            this.configuration = configuration;
            this.basePath = configuration.basePath || this.basePath;
        }
    }
}

/**
 *
 * @export
 * @class RequiredError
 * @extends {Error}
 */
export class RequiredError extends Error {
    name = "RequiredError"
    constructor(public field: string, msg?: string) {
        super(msg);
    }
}

/**
 * 
 * @export
 * @interface Identifiable
 */
export interface Identifiable {
    /**
     * 
     * @type {number}
     * @memberof Identifiable
     */
    id?: number;
}
/**
 * 
 * @export
 * @interface InlineResponse404
 */
export interface InlineResponse404 {
    /**
     * 
     * @type {number}
     * @memberof InlineResponse404
     */
    statusCode?: number;
    /**
     * 
     * @type {InlineResponse404Error}
     * @memberof InlineResponse404
     */
    error?: InlineResponse404Error;
}
/**
 * 
 * @export
 * @interface InlineResponse404Error
 */
export interface InlineResponse404Error {
    /**
     * 
     * @type {string}
     * @memberof InlineResponse404Error
     */
    message?: string;
}
/**
 * 
 * @export
 * @interface InlineResponse500
 */
export interface InlineResponse500 {
    /**
     * 
     * @type {number}
     * @memberof InlineResponse500
     */
    statusCode?: number;
    /**
     * 
     * @type {string}
     * @memberof InlineResponse500
     */
    message?: string;
}
/**
 * 
 * @export
 * @interface NameAndDescription
 */
export interface NameAndDescription {
    /**
     * 
     * @type {string}
     * @memberof NameAndDescription
     */
    name: string;
    /**
     * 
     * @type {string}
     * @memberof NameAndDescription
     */
    description?: string;
}
/**
 * 
 * @export
 * @interface Report
 */
export interface Report extends Identifiable {
    /**
     * 
     * @type {string}
     * @memberof Report
     */
    name: string;
    /**
     * 
     * @type {string}
     * @memberof Report
     */
    description?: string;
    /**
     * 
     * @type {Array<ReportParameter>}
     * @memberof Report
     */
    parameters?: Array<ReportParameter>;
}
/**
 * 
 * @export
 * @interface ReportFill
 */
export interface ReportFill extends Identifiable {
    /**
     * 
     * @type {number}
     * @memberof ReportFill
     */
    reportId?: number;
    /**
     * 
     * @type {Array<ReportFillParameter>}
     * @memberof ReportFill
     */
    fillParameters: Array<ReportFillParameter>;
}
/**
 * Parameters needed to generate a report fill
 * @export
 * @interface ReportFillParameter
 */
export interface ReportFillParameter extends null<String, any> {
    [key: string]: any;

}
/**
 * 
 * @export
 * @interface ReportFillResource
 */
export interface ReportFillResource {
    /**
     * 
     * @type {string}
     * @memberof ReportFillResource
     */
    url?: string;
}
/**
 * 
 * @export
 * @interface ReportParameter
 */
export interface ReportParameter {
    /**
     * 
     * @type {string}
     * @memberof ReportParameter
     */
    name: string;
    /**
     * 
     * @type {string}
     * @memberof ReportParameter
     */
    type: string;
}
/**
 * 
 * @export
 * @interface ReportUpload
 */
export interface ReportUpload extends Report {
    /**
     * 
     * @type {Blob}
     * @memberof ReportUpload
     */
    file?: Blob;
}
/**
 * ReportApi - fetch parameter creator
 * @export
 */
export const ReportApiFetchParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * Creates a new report template
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        addReport(options: any = {}): FetchArgs {
            const localVarPath = `/report`;
            const localVarUrlObj = url.parse(localVarPath, true);
            const localVarRequestOptions = Object.assign({ method: 'POST' }, options);
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            localVarUrlObj.query = Object.assign({}, localVarUrlObj.query, localVarQueryParameter, options.query);
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            localVarUrlObj.search = null;
            localVarRequestOptions.headers = Object.assign({}, localVarHeaderParameter, options.headers);

            return {
                url: url.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Deletes all fills of a given report
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteFills(reportId: number, options: any = {}): FetchArgs {
            // verify required parameter 'reportId' is not null or undefined
            if (reportId === null || reportId === undefined) {
                throw new RequiredError('reportId','Required parameter reportId was null or undefined when calling deleteFills.');
            }
            const localVarPath = `/report/{reportId}/fill`
                .replace(`{${"reportId"}}`, encodeURIComponent(String(reportId)));
            const localVarUrlObj = url.parse(localVarPath, true);
            const localVarRequestOptions = Object.assign({ method: 'DELETE' }, options);
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            localVarUrlObj.query = Object.assign({}, localVarUrlObj.query, localVarQueryParameter, options.query);
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            localVarUrlObj.search = null;
            localVarRequestOptions.headers = Object.assign({}, localVarHeaderParameter, options.headers);

            return {
                url: url.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Deletes a report and all of its fills from the server
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteReport(reportId: number, options: any = {}): FetchArgs {
            // verify required parameter 'reportId' is not null or undefined
            if (reportId === null || reportId === undefined) {
                throw new RequiredError('reportId','Required parameter reportId was null or undefined when calling deleteReport.');
            }
            const localVarPath = `/report/{reportId}`
                .replace(`{${"reportId"}}`, encodeURIComponent(String(reportId)));
            const localVarUrlObj = url.parse(localVarPath, true);
            const localVarRequestOptions = Object.assign({ method: 'DELETE' }, options);
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            localVarUrlObj.query = Object.assign({}, localVarUrlObj.query, localVarQueryParameter, options.query);
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            localVarUrlObj.search = null;
            localVarRequestOptions.headers = Object.assign({}, localVarHeaderParameter, options.headers);

            return {
                url: url.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Fills a report template with the provided parameters
         * @param {Array<ReportFillParameter>} body 
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        fillReport(body: Array<ReportFillParameter>, reportId: number, options: any = {}): FetchArgs {
            // verify required parameter 'body' is not null or undefined
            if (body === null || body === undefined) {
                throw new RequiredError('body','Required parameter body was null or undefined when calling fillReport.');
            }
            // verify required parameter 'reportId' is not null or undefined
            if (reportId === null || reportId === undefined) {
                throw new RequiredError('reportId','Required parameter reportId was null or undefined when calling fillReport.');
            }
            const localVarPath = `/report/{reportId}/fill`
                .replace(`{${"reportId"}}`, encodeURIComponent(String(reportId)));
            const localVarUrlObj = url.parse(localVarPath, true);
            const localVarRequestOptions = Object.assign({ method: 'POST' }, options);
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            localVarHeaderParameter['Content-Type'] = 'application/json';

            localVarUrlObj.query = Object.assign({}, localVarUrlObj.query, localVarQueryParameter, options.query);
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            localVarUrlObj.search = null;
            localVarRequestOptions.headers = Object.assign({}, localVarHeaderParameter, options.headers);
            const needsSerialization = (<any>"Array&lt;ReportFillParameter&gt;" !== "string") || localVarRequestOptions.headers['Content-Type'] === 'application/json';
            localVarRequestOptions.body =  needsSerialization ? JSON.stringify(body || {}) : (body || "");

            return {
                url: url.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Returns a specific report template
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getReport(reportId: number, options: any = {}): FetchArgs {
            // verify required parameter 'reportId' is not null or undefined
            if (reportId === null || reportId === undefined) {
                throw new RequiredError('reportId','Required parameter reportId was null or undefined when calling getReport.');
            }
            const localVarPath = `/report/{reportId}`
                .replace(`{${"reportId"}}`, encodeURIComponent(String(reportId)));
            const localVarUrlObj = url.parse(localVarPath, true);
            const localVarRequestOptions = Object.assign({ method: 'GET' }, options);
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            localVarUrlObj.query = Object.assign({}, localVarUrlObj.query, localVarQueryParameter, options.query);
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            localVarUrlObj.search = null;
            localVarRequestOptions.headers = Object.assign({}, localVarHeaderParameter, options.headers);

            return {
                url: url.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getReports(options: any = {}): FetchArgs {
            const localVarPath = `/report`;
            const localVarUrlObj = url.parse(localVarPath, true);
            const localVarRequestOptions = Object.assign({ method: 'GET' }, options);
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            localVarUrlObj.query = Object.assign({}, localVarUrlObj.query, localVarQueryParameter, options.query);
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            localVarUrlObj.search = null;
            localVarRequestOptions.headers = Object.assign({}, localVarHeaderParameter, options.headers);

            return {
                url: url.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Updates a report on the server
         * @param {NameAndDescription} body 
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        updateReport(body: NameAndDescription, reportId: number, options: any = {}): FetchArgs {
            // verify required parameter 'body' is not null or undefined
            if (body === null || body === undefined) {
                throw new RequiredError('body','Required parameter body was null or undefined when calling updateReport.');
            }
            // verify required parameter 'reportId' is not null or undefined
            if (reportId === null || reportId === undefined) {
                throw new RequiredError('reportId','Required parameter reportId was null or undefined when calling updateReport.');
            }
            const localVarPath = `/report/{reportId}`
                .replace(`{${"reportId"}}`, encodeURIComponent(String(reportId)));
            const localVarUrlObj = url.parse(localVarPath, true);
            const localVarRequestOptions = Object.assign({ method: 'PUT' }, options);
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            localVarHeaderParameter['Content-Type'] = 'application/json';

            localVarUrlObj.query = Object.assign({}, localVarUrlObj.query, localVarQueryParameter, options.query);
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            localVarUrlObj.search = null;
            localVarRequestOptions.headers = Object.assign({}, localVarHeaderParameter, options.headers);
            const needsSerialization = (<any>"NameAndDescription" !== "string") || localVarRequestOptions.headers['Content-Type'] === 'application/json';
            localVarRequestOptions.body =  needsSerialization ? JSON.stringify(body || {}) : (body || "");

            return {
                url: url.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * ReportApi - functional programming interface
 * @export
 */
export const ReportApiFp = function(configuration?: Configuration) {
    return {
        /**
         * Creates a new report template
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        addReport(options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<Report> {
            const localVarFetchArgs = ReportApiFetchParamCreator(configuration).addReport(options);
            return (fetch: FetchAPI = isomorphicFetch, basePath: string = BASE_PATH) => {
                return fetch(basePath + localVarFetchArgs.url, localVarFetchArgs.options).then((response) => {
                    if (response.status >= 200 && response.status < 300) {
                        return response.json();
                    } else {
                        throw response;
                    }
                });
            };
        },
        /**
         * Deletes all fills of a given report
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteFills(reportId: number, options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<Response> {
            const localVarFetchArgs = ReportApiFetchParamCreator(configuration).deleteFills(reportId, options);
            return (fetch: FetchAPI = isomorphicFetch, basePath: string = BASE_PATH) => {
                return fetch(basePath + localVarFetchArgs.url, localVarFetchArgs.options).then((response) => {
                    if (response.status >= 200 && response.status < 300) {
                        return response;
                    } else {
                        throw response;
                    }
                });
            };
        },
        /**
         * Deletes a report and all of its fills from the server
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteReport(reportId: number, options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<Response> {
            const localVarFetchArgs = ReportApiFetchParamCreator(configuration).deleteReport(reportId, options);
            return (fetch: FetchAPI = isomorphicFetch, basePath: string = BASE_PATH) => {
                return fetch(basePath + localVarFetchArgs.url, localVarFetchArgs.options).then((response) => {
                    if (response.status >= 200 && response.status < 300) {
                        return response;
                    } else {
                        throw response;
                    }
                });
            };
        },
        /**
         * Fills a report template with the provided parameters
         * @param {Array<ReportFillParameter>} body 
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        fillReport(body: Array<ReportFillParameter>, reportId: number, options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<ReportFill> {
            const localVarFetchArgs = ReportApiFetchParamCreator(configuration).fillReport(body, reportId, options);
            return (fetch: FetchAPI = isomorphicFetch, basePath: string = BASE_PATH) => {
                return fetch(basePath + localVarFetchArgs.url, localVarFetchArgs.options).then((response) => {
                    if (response.status >= 200 && response.status < 300) {
                        return response.json();
                    } else {
                        throw response;
                    }
                });
            };
        },
        /**
         * Returns a specific report template
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getReport(reportId: number, options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<Array<Report>> {
            const localVarFetchArgs = ReportApiFetchParamCreator(configuration).getReport(reportId, options);
            return (fetch: FetchAPI = isomorphicFetch, basePath: string = BASE_PATH) => {
                return fetch(basePath + localVarFetchArgs.url, localVarFetchArgs.options).then((response) => {
                    if (response.status >= 200 && response.status < 300) {
                        return response.json();
                    } else {
                        throw response;
                    }
                });
            };
        },
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getReports(options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<Array<Report>> {
            const localVarFetchArgs = ReportApiFetchParamCreator(configuration).getReports(options);
            return (fetch: FetchAPI = isomorphicFetch, basePath: string = BASE_PATH) => {
                return fetch(basePath + localVarFetchArgs.url, localVarFetchArgs.options).then((response) => {
                    if (response.status >= 200 && response.status < 300) {
                        return response.json();
                    } else {
                        throw response;
                    }
                });
            };
        },
        /**
         * Updates a report on the server
         * @param {NameAndDescription} body 
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        updateReport(body: NameAndDescription, reportId: number, options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<Report> {
            const localVarFetchArgs = ReportApiFetchParamCreator(configuration).updateReport(body, reportId, options);
            return (fetch: FetchAPI = isomorphicFetch, basePath: string = BASE_PATH) => {
                return fetch(basePath + localVarFetchArgs.url, localVarFetchArgs.options).then((response) => {
                    if (response.status >= 200 && response.status < 300) {
                        return response.json();
                    } else {
                        throw response;
                    }
                });
            };
        },
    }
};

/**
 * ReportApi - factory interface
 * @export
 */
export const ReportApiFactory = function (configuration?: Configuration, fetch?: FetchAPI, basePath?: string) {
    return {
        /**
         * Creates a new report template
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        addReport(options?: any) {
            return ReportApiFp(configuration).addReport(options)(fetch, basePath);
        },
        /**
         * Deletes all fills of a given report
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteFills(reportId: number, options?: any) {
            return ReportApiFp(configuration).deleteFills(reportId, options)(fetch, basePath);
        },
        /**
         * Deletes a report and all of its fills from the server
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteReport(reportId: number, options?: any) {
            return ReportApiFp(configuration).deleteReport(reportId, options)(fetch, basePath);
        },
        /**
         * Fills a report template with the provided parameters
         * @param {Array<ReportFillParameter>} body 
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        fillReport(body: Array<ReportFillParameter>, reportId: number, options?: any) {
            return ReportApiFp(configuration).fillReport(body, reportId, options)(fetch, basePath);
        },
        /**
         * Returns a specific report template
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getReport(reportId: number, options?: any) {
            return ReportApiFp(configuration).getReport(reportId, options)(fetch, basePath);
        },
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getReports(options?: any) {
            return ReportApiFp(configuration).getReports(options)(fetch, basePath);
        },
        /**
         * Updates a report on the server
         * @param {NameAndDescription} body 
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        updateReport(body: NameAndDescription, reportId: number, options?: any) {
            return ReportApiFp(configuration).updateReport(body, reportId, options)(fetch, basePath);
        },
    };
};

/**
 * ReportApi - object-oriented interface
 * @export
 * @class ReportApi
 * @extends {BaseAPI}
 */
export class ReportApi extends BaseAPI {
    /**
     * Creates a new report template
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ReportApi
     */
    public addReport(options?: any) {
        return ReportApiFp(this.configuration).addReport(options)(this.fetch, this.basePath);
    }

    /**
     * Deletes all fills of a given report
     * @param {number} reportId 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ReportApi
     */
    public deleteFills(reportId: number, options?: any) {
        return ReportApiFp(this.configuration).deleteFills(reportId, options)(this.fetch, this.basePath);
    }

    /**
     * Deletes a report and all of its fills from the server
     * @param {number} reportId 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ReportApi
     */
    public deleteReport(reportId: number, options?: any) {
        return ReportApiFp(this.configuration).deleteReport(reportId, options)(this.fetch, this.basePath);
    }

    /**
     * Fills a report template with the provided parameters
     * @param {Array<ReportFillParameter>} body 
     * @param {number} reportId 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ReportApi
     */
    public fillReport(body: Array<ReportFillParameter>, reportId: number, options?: any) {
        return ReportApiFp(this.configuration).fillReport(body, reportId, options)(this.fetch, this.basePath);
    }

    /**
     * Returns a specific report template
     * @param {number} reportId 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ReportApi
     */
    public getReport(reportId: number, options?: any) {
        return ReportApiFp(this.configuration).getReport(reportId, options)(this.fetch, this.basePath);
    }

    /**
     * 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ReportApi
     */
    public getReports(options?: any) {
        return ReportApiFp(this.configuration).getReports(options)(this.fetch, this.basePath);
    }

    /**
     * Updates a report on the server
     * @param {NameAndDescription} body 
     * @param {number} reportId 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ReportApi
     */
    public updateReport(body: NameAndDescription, reportId: number, options?: any) {
        return ReportApiFp(this.configuration).updateReport(body, reportId, options)(this.fetch, this.basePath);
    }

}
/**
 * ReportFillApi - fetch parameter creator
 * @export
 */
export const ReportFillApiFetchParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * Deletes all fills of a given report
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteFills(reportId: number, options: any = {}): FetchArgs {
            // verify required parameter 'reportId' is not null or undefined
            if (reportId === null || reportId === undefined) {
                throw new RequiredError('reportId','Required parameter reportId was null or undefined when calling deleteFills.');
            }
            const localVarPath = `/report/{reportId}/fill`
                .replace(`{${"reportId"}}`, encodeURIComponent(String(reportId)));
            const localVarUrlObj = url.parse(localVarPath, true);
            const localVarRequestOptions = Object.assign({ method: 'DELETE' }, options);
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            localVarUrlObj.query = Object.assign({}, localVarUrlObj.query, localVarQueryParameter, options.query);
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            localVarUrlObj.search = null;
            localVarRequestOptions.headers = Object.assign({}, localVarHeaderParameter, options.headers);

            return {
                url: url.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Deletes a filled report
         * @param {number} reportId 
         * @param {number} reportFillId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteReportFill(reportId: number, reportFillId: number, options: any = {}): FetchArgs {
            // verify required parameter 'reportId' is not null or undefined
            if (reportId === null || reportId === undefined) {
                throw new RequiredError('reportId','Required parameter reportId was null or undefined when calling deleteReportFill.');
            }
            // verify required parameter 'reportFillId' is not null or undefined
            if (reportFillId === null || reportFillId === undefined) {
                throw new RequiredError('reportFillId','Required parameter reportFillId was null or undefined when calling deleteReportFill.');
            }
            const localVarPath = `/report/{reportId}/fill/{reportFillId}`
                .replace(`{${"reportId"}}`, encodeURIComponent(String(reportId)))
                .replace(`{${"reportFillId"}}`, encodeURIComponent(String(reportFillId)));
            const localVarUrlObj = url.parse(localVarPath, true);
            const localVarRequestOptions = Object.assign({ method: 'DELETE' }, options);
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            localVarUrlObj.query = Object.assign({}, localVarUrlObj.query, localVarQueryParameter, options.query);
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            localVarUrlObj.search = null;
            localVarRequestOptions.headers = Object.assign({}, localVarHeaderParameter, options.headers);

            return {
                url: url.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Returns a specific filled report
         * @param {number} reportId 
         * @param {number} reportFillId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getReportFill(reportId: number, reportFillId: number, options: any = {}): FetchArgs {
            // verify required parameter 'reportId' is not null or undefined
            if (reportId === null || reportId === undefined) {
                throw new RequiredError('reportId','Required parameter reportId was null or undefined when calling getReportFill.');
            }
            // verify required parameter 'reportFillId' is not null or undefined
            if (reportFillId === null || reportFillId === undefined) {
                throw new RequiredError('reportFillId','Required parameter reportFillId was null or undefined when calling getReportFill.');
            }
            const localVarPath = `/report/{reportId}/fill/{reportFillId}`
                .replace(`{${"reportId"}}`, encodeURIComponent(String(reportId)))
                .replace(`{${"reportFillId"}}`, encodeURIComponent(String(reportFillId)));
            const localVarUrlObj = url.parse(localVarPath, true);
            const localVarRequestOptions = Object.assign({ method: 'GET' }, options);
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            localVarUrlObj.query = Object.assign({}, localVarUrlObj.query, localVarQueryParameter, options.query);
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            localVarUrlObj.search = null;
            localVarRequestOptions.headers = Object.assign({}, localVarHeaderParameter, options.headers);

            return {
                url: url.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * ReportFillApi - functional programming interface
 * @export
 */
export const ReportFillApiFp = function(configuration?: Configuration) {
    return {
        /**
         * Deletes all fills of a given report
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteFills(reportId: number, options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<Response> {
            const localVarFetchArgs = ReportFillApiFetchParamCreator(configuration).deleteFills(reportId, options);
            return (fetch: FetchAPI = isomorphicFetch, basePath: string = BASE_PATH) => {
                return fetch(basePath + localVarFetchArgs.url, localVarFetchArgs.options).then((response) => {
                    if (response.status >= 200 && response.status < 300) {
                        return response;
                    } else {
                        throw response;
                    }
                });
            };
        },
        /**
         * Deletes a filled report
         * @param {number} reportId 
         * @param {number} reportFillId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteReportFill(reportId: number, reportFillId: number, options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<Response> {
            const localVarFetchArgs = ReportFillApiFetchParamCreator(configuration).deleteReportFill(reportId, reportFillId, options);
            return (fetch: FetchAPI = isomorphicFetch, basePath: string = BASE_PATH) => {
                return fetch(basePath + localVarFetchArgs.url, localVarFetchArgs.options).then((response) => {
                    if (response.status >= 200 && response.status < 300) {
                        return response;
                    } else {
                        throw response;
                    }
                });
            };
        },
        /**
         * Returns a specific filled report
         * @param {number} reportId 
         * @param {number} reportFillId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getReportFill(reportId: number, reportFillId: number, options?: any): (fetch?: FetchAPI, basePath?: string) => Promise<Array<ReportFill & ReportFillResource>> {
            const localVarFetchArgs = ReportFillApiFetchParamCreator(configuration).getReportFill(reportId, reportFillId, options);
            return (fetch: FetchAPI = isomorphicFetch, basePath: string = BASE_PATH) => {
                return fetch(basePath + localVarFetchArgs.url, localVarFetchArgs.options).then((response) => {
                    if (response.status >= 200 && response.status < 300) {
                        return response.json();
                    } else {
                        throw response;
                    }
                });
            };
        },
    }
};

/**
 * ReportFillApi - factory interface
 * @export
 */
export const ReportFillApiFactory = function (configuration?: Configuration, fetch?: FetchAPI, basePath?: string) {
    return {
        /**
         * Deletes all fills of a given report
         * @param {number} reportId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteFills(reportId: number, options?: any) {
            return ReportFillApiFp(configuration).deleteFills(reportId, options)(fetch, basePath);
        },
        /**
         * Deletes a filled report
         * @param {number} reportId 
         * @param {number} reportFillId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteReportFill(reportId: number, reportFillId: number, options?: any) {
            return ReportFillApiFp(configuration).deleteReportFill(reportId, reportFillId, options)(fetch, basePath);
        },
        /**
         * Returns a specific filled report
         * @param {number} reportId 
         * @param {number} reportFillId 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getReportFill(reportId: number, reportFillId: number, options?: any) {
            return ReportFillApiFp(configuration).getReportFill(reportId, reportFillId, options)(fetch, basePath);
        },
    };
};

/**
 * ReportFillApi - object-oriented interface
 * @export
 * @class ReportFillApi
 * @extends {BaseAPI}
 */
export class ReportFillApi extends BaseAPI {
    /**
     * Deletes all fills of a given report
     * @param {number} reportId 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ReportFillApi
     */
    public deleteFills(reportId: number, options?: any) {
        return ReportFillApiFp(this.configuration).deleteFills(reportId, options)(this.fetch, this.basePath);
    }

    /**
     * Deletes a filled report
     * @param {number} reportId 
     * @param {number} reportFillId 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ReportFillApi
     */
    public deleteReportFill(reportId: number, reportFillId: number, options?: any) {
        return ReportFillApiFp(this.configuration).deleteReportFill(reportId, reportFillId, options)(this.fetch, this.basePath);
    }

    /**
     * Returns a specific filled report
     * @param {number} reportId 
     * @param {number} reportFillId 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ReportFillApi
     */
    public getReportFill(reportId: number, reportFillId: number, options?: any) {
        return ReportFillApiFp(this.configuration).getReportFill(reportId, reportFillId, options)(this.fetch, this.basePath);
    }

}
