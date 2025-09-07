import type { LegacyEndpointPayload } from './types'

export async function fetchFromLegacyEndpoint({
  module,
  action,
  ...otherParams
}: LegacyEndpointPayload) {
  const response = await fetch(import.meta.env.VITE_BIBLIVRE_ENDPOINT, {
    method: 'POST',
    headers: {
      'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
    body: new URLSearchParams({
      controller: 'json',
      module,
      action,
      ...otherParams,
    }).toString(),
  })

  return response.json()
}
