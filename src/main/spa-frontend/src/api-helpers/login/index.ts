import { MODULES } from '../constants'
import { fetchJSONFromLegacyEndpoint } from '..'

import type {
  LoginSessionResponse,
  NonSuccessfulResponse,
  SuccessfulResponse,
} from '../types'

export async function fetchLoginSession(): Promise<LoginSessionResponse> {
  const data = await fetchJSONFromLegacyEndpoint({
    module: MODULES.LOGIN,
    action: 'session',
  })

  return data
}

export async function logoutWithLegacyEndpoint(): Promise<
  SuccessfulResponse | NonSuccessfulResponse
> {
  const data = await fetchJSONFromLegacyEndpoint({
    module: MODULES.LOGIN,
    action: 'logout',
  })

  return data
}

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

  return data
}
