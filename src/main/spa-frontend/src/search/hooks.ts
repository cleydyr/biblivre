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
  BibliographicMaterial,
  BibliographicRecord,
  SearchQueryTerms,
  SearchResponse,
} from '../api-helpers/search/types'

// Query keys for cache management
export const searchQueryKeys = {
  all: ['search'] as const,
  results: (materiaType: BibliographicMaterial, terms?: SearchQueryTerms) =>
    [...searchQueryKeys.all, 'results', materiaType, terms] as const,
  pagination: (searchId?: string, page?: number, sort?: number) =>
    [...searchQueryKeys.all, 'pagination', searchId, page, sort] as const,
  record: (recordId: string) =>
    [...searchQueryKeys.all, 'record', recordId] as const,
}

export function usePaginatedSearch(
  terms: SearchQueryTerms | undefined,
  page: number,
  materialType: BibliographicMaterial,
  sort?: number,
  options?: Omit<
    UseQueryOptions<SearchResponse>,
    'queryKey' | 'queryFn' | 'placeholderData'
  >,
) {
  const [searchId, setSearchId] = useState<string | undefined>(undefined)

  useEffect(() => {
    setSearchId(undefined)
  }, [terms, materialType])

  const initialQuery = useQuery({
    ...options,
    queryKey: searchQueryKeys.results(materialType, terms),
    queryFn: () => getCatalographicSearchResults(materialType, terms),
    enabled: options?.enabled && searchId === undefined,
    placeholderData: keepPreviousData,
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
    queryKey: searchQueryKeys.pagination(searchId, page, sort),
    queryFn: () =>
      paginateCatalographicSearchResults(searchId ?? '', page, sort),
    enabled: options?.enabled && searchId !== undefined && page > 0,
    placeholderData: keepPreviousData,
    staleTime: 60 * 1000,
  })

  return page > 0 ? paginatedSearchQuery : initialQuery
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
