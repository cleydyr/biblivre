import { MODULES } from '../constants'
import { fetchJSONFromLegacyEndpoint } from '..'

import type { NonSuccessfulResponse, SuccessfulResponse } from '../types'

export async function loginWithLegacyEndpoint({
  username,
  password,
}: {
  username: string
  password: string
}): Promise<SuccessfulResponse | NonSuccessfulResponse> {
  const data = await fetchJSONFromLegacyEndpoint({
    module: MODULES.LOGIN,
    action: 'login',
    username,
    password,
  })

  return data as SuccessfulResponse | NonSuccessfulResponse
}
