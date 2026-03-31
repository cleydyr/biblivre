import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'

import {
  fetchLoginSession,
  loginWithLegacyEndpoint,
  logoutWithLegacyEndpoint,
} from '.'

import type { UseMutationOptions } from '@tanstack/react-query'

import type { LoginSessionResponse, NonSuccessfulResponse, SuccessfulResponse } from '../types'

export const AUTH_SESSION_QUERY_KEY = ['auth', 'session'] as const

export function useAuthSession() {
  return useQuery<LoginSessionResponse, NonSuccessfulResponse>({
    queryKey: AUTH_SESSION_QUERY_KEY,
    queryFn: fetchLoginSession,
    refetchOnWindowFocus: true,
    staleTime: 30_000,
  })
}

export function useLegacyLogout(
  options?: Omit<
    UseMutationOptions<SuccessfulResponse, Error, void>,
    'mutationFn'
  >,
) {
  const queryClient = useQueryClient()

  return useMutation({
    ...options,
    mutationFn: async () => {
      const data = await logoutWithLegacyEndpoint()

      if (!data.success) {
        throw new Error(data.message)
      }

      return data
    },
    onSuccess: (data, variables, context) => {
      void queryClient.invalidateQueries()
      options?.onSuccess?.(data, variables, context)
    },
  })
}

export function useLegacyLogin(
  options?: Omit<
    UseMutationOptions<
      SuccessfulResponse,
      Error,
      { username: string; password: string }
    >,
    'mutationFn'
  >,
) {
  const queryClient = useQueryClient()

  return useMutation({
    ...options,
    mutationFn: async ({ username, password }) => {
      const data = await loginWithLegacyEndpoint({ username, password })

      if (!data.success) {
        throw new Error(data.message)
      }

      return data
    },
    onSuccess: (data, variables, context) => {
      queryClient.invalidateQueries()
      options?.onSuccess?.(data, variables, context)
    },
  })
}
