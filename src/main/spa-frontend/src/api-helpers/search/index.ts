import { downloadFromLegacyEndpoint, fetchJSONFromLegacyEndpoint } from '..'

import { ACTIONS, FIELDS } from './constants'
import { getSearchMode, getSearchTerms } from './lib'

import type { UUID } from '../../types'
import type { FileDownload } from '../types'

import type { ExportResponse, OpenResponse } from './response-types'
import type {
  BibliographicMaterial,
  EncodedQueryField,
  SearchQueryTerms,
  SearchResponse,
} from './types'

export async function getCatalographicSearchResults(
  materialType: BibliographicMaterial,
  terms?: SearchQueryTerms,
): Promise<SearchResponse> {
  return fetchJSONFromLegacyEndpoint({
    module: 'cataloging.bibliographic',
    action: ACTIONS.SEARCH,
    search_parameters: JSON.stringify({
      database: 'main',
      material_type: materialType,
      search_mode: getSearchMode(terms),
      ...getSearchTerms(terms),
    }),
  })
}

export async function paginateCatalographicSearchResults(
  search_id: string,
  page: number,
  sort?: EncodedQueryField,
): Promise<SearchResponse> {
  return fetchJSONFromLegacyEndpoint({
    module: 'cataloging.bibliographic',
    action: ACTIONS.PAGINATE,
    page: String(page + 1),
    search_id,
    sort: sort ? String(sort) : FIELDS.TITLE,
  })
}

export function openBibliographicRecord(
  recordId: string,
): Promise<OpenResponse> {
  return fetchJSONFromLegacyEndpoint({
    module: 'cataloging.bibliographic',
    action: ACTIONS.OPEN,
    id: recordId,
  })
}

export function exportBibliographicRecords(
  recordIds: number[],
): Promise<ExportResponse> {
  return fetchJSONFromLegacyEndpoint({
    module: 'cataloging.bibliographic',
    action: ACTIONS.EXPORT,
    id_list: recordIds.join(','),
  })
}

export function downloadExport(uuid: UUID): Promise<FileDownload> {
  return downloadFromLegacyEndpoint({
    module: 'cataloging.bibliographic',
    action: ACTIONS.DOWNLOAD_EXPORT,
    id: uuid,
  })
}

export async function prepareSearchExcelExport(
  search_parameters: string,
  sort: string,
): Promise<ExportResponse> {
  return fetchJSONFromLegacyEndpoint({
    module: 'cataloging.bibliographic',
    action: ACTIONS.EXPORT_SEARCH_EXCEL,
    search_parameters,
    sort,
  })
}

export function downloadSearchExcel(uuid: UUID): Promise<FileDownload> {
  return downloadFromLegacyEndpoint({
    module: 'cataloging.bibliographic',
    action: ACTIONS.DOWNLOAD_SEARCH_EXCEL,
    id: uuid,
  })
}
