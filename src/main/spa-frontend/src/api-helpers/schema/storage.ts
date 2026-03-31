import { SCHEMA_STORAGE_KEY } from '../constants'

export function getStoredSchema(): string | null {
  try {
    const value = globalThis.localStorage?.getItem(SCHEMA_STORAGE_KEY)
    return value && value.length > 0 ? value : null
  } catch {
    return null
  }
}

export function setStoredSchema(schema: string): void {
  globalThis.localStorage?.setItem(SCHEMA_STORAGE_KEY, schema)
}

export function clearStoredSchema(): void {
  globalThis.localStorage?.removeItem(SCHEMA_STORAGE_KEY)
}
