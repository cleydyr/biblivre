export const MODULES = {
  CATALOGING_BIBLIOGRAPHIC: 'cataloging.bibliographic',
  MENU: 'menu',
  LOGIN: 'login',
  MULTI_SCHEMA: 'multi_schema',
} as const

export const DEFAULT_HEADERS = {
  'content-type': 'application/x-www-form-urlencoded;charset=UTF-8',
} as const

export const SCHEMA_STORAGE_KEY = 'biblivre.selectedSchema'
