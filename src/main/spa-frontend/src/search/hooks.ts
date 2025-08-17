import { useMutation, useQueryClient } from '@tanstack/react-query'

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

// Query keys for cache management
export const searchQueryKeys = {
  all: ['search'] as const,
  results: (query?: SearchQuery) =>
    [...searchQueryKeys.all, 'results', query] as const,
  pagination: (searchId: string, page: number) =>
    [...searchQueryKeys.all, 'pagination', searchId, page] as const,
  record: (recordId: string) =>
    [...searchQueryKeys.all, 'record', recordId] as const,
}

type PaginateSearchParams = {
  searchId: string
  page: number
}

// Hook for performing catalogographic search
export function useSearchCatalographic() {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: (query: SearchQuery | undefined) =>
      getCatalographicSearchResults(query),
    onSuccess: (data: SearchResponse, variables: SearchQuery | undefined) => {
      // Cache the search results
      queryClient.setQueryData(searchQueryKeys.results(variables), data)
    },
  })

  return {
    ...mutation,
    mutate: (query?: SearchQuery) => mutation.mutate(query),
    mutateAsync: (query?: SearchQuery) => mutation.mutateAsync(query),
  }
}

// Hook for paginating search results
export function usePaginateSearchResults() {
  const queryClient = useQueryClient()

  return useMutation<SearchResponse, unknown, PaginateSearchParams>({
    mutationFn: ({ searchId, page }: PaginateSearchParams) =>
      paginateCatalographicSearchResults(searchId, page),
    onSuccess: (data, variables) => {
      // Cache the paginated results
      queryClient.setQueryData(
        searchQueryKeys.pagination(variables.searchId, variables.page),
        data
      )
    },
  })
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
