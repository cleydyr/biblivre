import {
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
  type UseQueryOptions,
} from '@tanstack/react-query'
import { useEffect, useState } from 'react'

import {
  getCatalographicSearchResults,
  openBibliographicRecord,
  paginateCatalographicSearchResults,
} from '../api-helpers/search'

import type {
  BibliographicRecord,
  SearchQuery,
  SearchResponse,
} from '../api-helpers/search/types'
import type { PaginateSearchParams } from '../api-helpers/types'

// Query keys for cache management
export const searchQueryKeys = {
  all: ['search'] as const,
  results: (query?: SearchQuery) =>
    [...searchQueryKeys.all, 'results', query] as const,
  pagination: (searchId: string, page: number, sort?: number) =>
    [...searchQueryKeys.all, 'pagination', searchId, page, sort] as const,
  record: (recordId: string) =>
    [...searchQueryKeys.all, 'record', recordId] as const,
}

// Hook for performing catalogographic search
export function useSearchCatalographic(
  query?: SearchQuery,
  options?: Omit<UseQueryOptions<SearchResponse>, 'queryKey'>
) {
  return useQuery({
    queryKey: searchQueryKeys.results(),
    queryFn: () => {
      return getCatalographicSearchResults(query)
    },
    ...options,
  })
}

// Hook for paginating search results
export function usePaginateSearchResults(
  { searchId, page }: PaginateSearchParams,
  options?: Omit<UseQueryOptions<SearchResponse>, 'queryKey'>
) {
  return useQuery({
    queryKey: searchQueryKeys.pagination(searchId, page),
    queryFn: () => paginateCatalographicSearchResults(searchId, page),
    ...options,
  })
}

export function usePaginatedSearch(
  query: SearchQuery | undefined,
  page: number,
  sort?: number,
  options?: Omit<
    UseQueryOptions<SearchResponse>,
    'queryKey' | 'queryFn' | 'placeholderData'
  >
) {
  const [searchId, setSearchId] = useState<string | undefined>(undefined)

  useEffect(() => {
    setSearchId(undefined)
  }, [query])

  const initialQuery = useQuery({
    ...options,
    queryKey: searchQueryKeys.results(query),
    queryFn: () => getCatalographicSearchResults(query),
    enabled: options?.enabled && searchId === undefined,
  })

  useEffect(() => {
    if (
      initialQuery.isSuccess &&
      initialQuery.data.success &&
      initialQuery.data.search.page_count > 0
    ) {
      setSearchId(String(initialQuery.data.search.id))
    }
  }, [initialQuery.isSuccess, initialQuery.data])

  const paginatedSearchQuery = useQuery({
    ...options,
    queryKey: searchQueryKeys.pagination(searchId ?? '', page, sort),
    queryFn: () =>
      paginateCatalographicSearchResults(searchId ?? '', page, sort),
    enabled: options?.enabled && searchId !== undefined,
    placeholderData: keepPreviousData,
  })

  return searchId ? paginatedSearchQuery : initialQuery
}

// Hook for opening a bibliographic record
export function useOpenBibliographicRecord() {
  const queryClient = useQueryClient()

  return useMutation<BibliographicRecord, unknown, string>({
    mutationFn: (recordId: string) => openBibliographicRecord(recordId),
    onSuccess: (data, recordId) => {
      // Cache the opened record
      queryClient.setQueryData(searchQueryKeys.record(recordId), data)
    },
  })
}
