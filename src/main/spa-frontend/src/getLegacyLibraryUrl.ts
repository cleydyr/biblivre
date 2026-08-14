import type { SchemaListItem } from './api-helpers/types'

export function getLegacyRootUrl(endpoint: string): string {
  return endpoint.endsWith('/') ? endpoint : `${endpoint}/`
}

export function getLegacyLibraryUrl(
  schemas: SchemaListItem[] | undefined,
  activeSchemaId: string | null,
  endpoint: string,
): string {
  const rootUrl = getLegacyRootUrl(endpoint)

  if (!schemas || !activeSchemaId) {
    return rootUrl
  }

  const schema = schemas.find((item) => item.schema === activeSchemaId)

  if (!schema) {
    return rootUrl
  }

  return `${endpoint.replace(/\/$/, '')}/${schema.schema}`
}
