import type { InitOverrideFunction } from '../generated-sources'

import { buildRestApiHeaders } from '.'

export const defaultRestApiFetchOptions: InitOverrideFunction = async ({
  init,
}) => {
  const existingHeaders = new Headers(init?.headers)
  const headers: Record<string, string> = Object.fromEntries(
    existingHeaders.entries(),
  )

  return {
    ...init,
    headers: {
      ...headers,
      Accept: 'application/json',
      ...buildRestApiHeaders(),
    },
    credentials: 'include',
  }
}
