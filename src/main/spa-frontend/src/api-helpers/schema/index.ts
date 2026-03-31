import { MODULES } from '../constants'
import { fetchJSONFromLegacyEndpoint } from '..'

import type { SchemasListResponse } from '../types'

export async function fetchSchemasList(): Promise<SchemasListResponse> {
  const data = await fetchJSONFromLegacyEndpoint({
    module: MODULES.MULTI_SCHEMA,
    action: 'list',
  })

  return data as SchemasListResponse
}
