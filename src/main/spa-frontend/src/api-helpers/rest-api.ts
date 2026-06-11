import type { InitOverrideFunction } from '../generated-sources'

import { buildDefaultHeaders } from '.'

export const defaultRestApiFetchOptions: InitOverrideFunction = async ({
  init,
}) => ({
  ...init,
  headers: {
    ...(init.headers as Record<string, string>),
    Accept: 'application/json',
    ...buildDefaultHeaders(),
  },
  credentials: 'include',
})
