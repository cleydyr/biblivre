import { useMutation, useQuery } from '@tanstack/react-query'

import {
  blockCirculationUser,
  deleteCirculationUser,
  getCirculationUsersPaginate,
  getCirculationUsersSearch,
  getCirculationUserTabData,
  saveCirculationUser,
  unblockCirculationUser,
} from '.'

import type {
  CirculationPaginatePayload,
  CirculationSearchPayload,
  CirculationUserSavePayload,
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

export const useBlockCirculationUserMutation = () => {
  return useMutation({
    mutationFn: blockCirculationUser,
  })
}

export const useUnblockCirculationUserMutation = () => {
  return useMutation({
    mutationFn: unblockCirculationUser,
  })
}

export const useDeleteCirculationUserMutation = () => {
  return useMutation({
    mutationFn: deleteCirculationUser,
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

export function useSaveCirculationUserMutation() {
  return useMutation({
    mutationFn: (payload: CirculationUserSavePayload) =>
      saveCirculationUser(payload),
  })
}
