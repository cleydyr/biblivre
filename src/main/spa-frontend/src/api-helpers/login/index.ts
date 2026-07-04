import { fetchJSONFromLegacyEndpoint } from '..'

import type {
  LoginSessionResponse,
  NonSuccessfulResponse,
  SuccessfulResponse,
} from '../types'

export async function fetchLoginSession(): Promise<LoginSessionResponse> {
  const data = await fetchJSONFromLegacyEndpoint({
    module: 'login',
    action: 'session',
  })

  return data
}

export async function logoutWithLegacyEndpoint(): Promise<
  SuccessfulResponse | NonSuccessfulResponse
> {
  const data = await fetchJSONFromLegacyEndpoint({
    module: 'login',
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
    module: 'login',
    action: 'login',
    username,
    password,
  })

  return data
}
