import type { SchemaListItem } from './api-helpers/types'

export function getLegacyLibraryUrl(
  schemas: SchemaListItem[] | undefined,
  activeSchemaId: string | null,
  endpoint: string,
): string | undefined {
  if (!schemas || !activeSchemaId) {
    return undefined
  }

  const schema = schemas.find((item) => item.schema === activeSchemaId)

  if (!schema) {
    return undefined
  }

  return `${endpoint}/${schema.schema}`
}
