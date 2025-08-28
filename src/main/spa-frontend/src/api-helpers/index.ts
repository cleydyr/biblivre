import type { LegacyModule } from './types'

export async function fetchFromLegacyEndpoint(
  module: LegacyModule,
  action: string,
  otherParams: Record<string, string> = {}
) {
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
