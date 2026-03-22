import { useMutation } from '@tanstack/react-query'

import { loginWithLegacyEndpoint } from '.'

import type { UseMutationOptions } from '@tanstack/react-query'

import type { SuccessfulResponse } from '../types'

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
  return useMutation({
    ...options,
    mutationFn: async ({ username, password }) => {
      const data = await loginWithLegacyEndpoint({ username, password })

      if (!data.success) {
        throw new Error(data.message)
      }

      return data
    },
  })
}
