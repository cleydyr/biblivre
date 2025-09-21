import { keepPreviousData, useMutation, useQuery } from '@tanstack/react-query'
import { useEffect, useState } from 'react'

import { downloadFile } from '../api-helpers/lib'
import {
  downloadExport,
  exportBibliographicRecords,
  getCatalographicSearchResults,
  openBibliographicRecord,
  paginateCatalographicSearchResults,
} from '../api-helpers/search'

import type { UseMutationOptions, UseQueryOptions } from '@tanstack/react-query'

import type { OpenResponse } from '../api-helpers/search/response-types'
import type {
  BibliographicMaterial,
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
  record: (recordId: number) =>
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
export function useOpenBibliographicRecord(
  recordId: number,
  options?: Omit<
    UseQueryOptions<OpenResponse>,
    'queryKey' | 'queryFn' | 'staleTime'
  >,
) {
  return useQuery({
    ...options,
    queryKey: searchQueryKeys.record(recordId),
    queryFn: () => openBibliographicRecord(String(recordId)),
    staleTime: 60 * 1000,
  })
}

export function useExportAndDownloadMutation(
  options?: Omit<UseMutationOptions<void, Error, number[]>, 'mutationFn'>,
) {
  return useMutation({
    ...options,
    mutationFn: async (recordIds: number[]) => {
      const exportResponse = await exportBibliographicRecords(recordIds)

      if (!exportResponse.success) {
        throw new Error(exportResponse.message)
      }

      const downloadExportResponse = await downloadExport(exportResponse.uuid)

      downloadFile(downloadExportResponse)
    },
  })
}
