import type { Pagination } from '@elastic/eui'

import type {
  SearchResponse,
  SuccessfulSearchResponse,
} from '../api-helpers/search/response-types'

export function isFirstResult(
  searchResults: SearchResponse | undefined,
  recordIdForFlyout: number | undefined,
) {
  if (searchResults === undefined || !searchResults.success) {
    return false
  }

  const { search } = searchResults

  if (search.page !== 1) {
    return false
  }

  return (
    search.data.findIndex((record) => record.id === recordIdForFlyout) === 0
  )
}

export function isLastResult(
  searchResults: SearchResponse | undefined,
  recordIdForFlyout: number | undefined,
) {
  if (searchResults === undefined || !searchResults.success) {
    return false
  }

  const { search } = searchResults

  if (search.page !== search.page_count) {
    return false
  }

  return (
    search.data.findIndex((record) => record.id === recordIdForFlyout) ===
    search.data.length - 1
  )
}

export function getPagination(
  searchResults: SuccessfulSearchResponse,
): Pagination | undefined {
  if (searchResults.search.page_count <= 0) {
    return undefined
  }

  return {
    pageIndex: searchResults.search.page - 1,
    totalItemCount: searchResults.search.record_count,
    pageSize: searchResults.search.records_per_page,
    showPerPageOptions: false,
  }
}
