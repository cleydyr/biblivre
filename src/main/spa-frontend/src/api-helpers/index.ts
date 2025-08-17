import type { LegacyModule } from './types'

export async function fetchFromLegacyEndpoint(
  host: string,
  module: LegacyModule,
  action: string,
  otherParams: Record<string, string> = {}
) {
  const response = await fetch(host, {
    method: 'POST',
    headers: {
      'content-type': 'application/x-www-form-urlencoded; charset=UTF-8',
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
