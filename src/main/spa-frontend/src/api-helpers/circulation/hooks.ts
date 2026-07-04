import { useMutation, useQuery } from '@tanstack/react-query'

import {
  getCirculationUsersPaginate,
  getCirculationUsersSearch,
  getCirculationUserTabData,
} from '.'

import type {
  CirculationPaginatePayload,
  CirculationSearchPayload,
  CirculationUserTab,
} from './types'

export const useCirculationUsersSearchMutation = () => {
  return useMutation({
    mutationFn: async (payload: CirculationSearchPayload) => {
      return getCirculationUsersSearch(payload)
    },
  })
}

export const useCirculationUsersPaginateMutation = () => {
  return useMutation({
    mutationFn: async (payload: CirculationPaginatePayload) => {
      return getCirculationUsersPaginate(payload)
    },
  })
}

export const circulationQueryKeys = {
  all: ['circulation'] as const,
  userTabData: (userId: number, tab: CirculationUserTab) =>
    [...circulationQueryKeys.all, 'user-tab-data', userId, tab] as const,
}

export function useCirculationUserTabData<T extends CirculationUserTab>(
  userId: number,
  tab: T,
  enabled = true,
) {
  return useQuery({
    queryKey: circulationQueryKeys.userTabData(userId, tab),
    queryFn: async () => {
      return getCirculationUserTabData(userId, tab)
    },
    enabled,
    staleTime: 60 * 1000,
  })
}
