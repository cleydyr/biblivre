import { fetchJSONFromLegacyEndpoint } from '..'

import type { SchemasListResponse } from '../types'

export async function fetchSchemasList(): Promise<SchemasListResponse> {
  const data = await fetchJSONFromLegacyEndpoint({
    module: 'multi_schema',
    action: 'list',
  })

  return data as SchemasListResponse
}
