import type { ParametrizedLegacyEndpointPayload } from '../types'

import type { ACTIONS } from './constants'

type SearchParametrizedLegacyEndpointPayload =
  ParametrizedLegacyEndpointPayload<
    'cataloging.bibliographic',
    typeof ACTIONS.SEARCH,
    'search_parameters'
  >

type SearchPaginatePayload = ParametrizedLegacyEndpointPayload<
  'cataloging.bibliographic',
  typeof ACTIONS.PAGINATE,
  'search_id' | 'page'
>

type SearchPaginateSortPayload = ParametrizedLegacyEndpointPayload<
  'cataloging.bibliographic',
  typeof ACTIONS.PAGINATE,
  'search_id' | 'page' | 'sort'
>

type SearchOpenPayload = ParametrizedLegacyEndpointPayload<
  'cataloging.bibliographic',
  typeof ACTIONS.OPEN,
  'id'
>

type SearchExportPayload = ParametrizedLegacyEndpointPayload<
  'cataloging.bibliographic',
  typeof ACTIONS.EXPORT,
  'id_list'
>

type SearchDownloadExportPayload = ParametrizedLegacyEndpointPayload<
  'cataloging.bibliographic',
  typeof ACTIONS.DOWNLOAD_EXPORT,
  'id'
>

type SearchExportSearchExcelPayload = ParametrizedLegacyEndpointPayload<
  'cataloging.bibliographic',
  typeof ACTIONS.EXPORT_SEARCH_EXCEL,
  'sort' | 'search_parameters'
>

type SearchDownloadSearchExcelPayload = ParametrizedLegacyEndpointPayload<
  'cataloging.bibliographic',
  typeof ACTIONS.DOWNLOAD_SEARCH_EXCEL,
  'id'
>

declare module '../../registry' {
  interface ParametrizedLegacyEndpointPayloadRegistry {
    SearchParametrizedLegacyEndpointPayload: SearchParametrizedLegacyEndpointPayload
    SearchPaginatePayload: SearchPaginatePayload
    SearchPaginateSortPayload: SearchPaginateSortPayload
    SearchOpenPayload: SearchOpenPayload
    SearchExportPayload: SearchExportPayload
    SearchDownloadExportPayload: SearchDownloadExportPayload
    SearchExportSearchExcelPayload: SearchExportSearchExcelPayload
    SearchDownloadSearchExcelPayload: SearchDownloadSearchExcelPayload
  }
}
