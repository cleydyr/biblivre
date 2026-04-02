/// <reference types="vite/client" />

interface ViteTypeOptions {
  // By adding this line, you can make the type of ImportMetaEnv strict
  // to disallow unknown keys.
  strictImportMetaEnv: unknown
}

interface ImportMetaEnv {
  readonly VITE_BIBLIVRE_ENDPOINT: string
  readonly VITE_FEATURE_SEARCH_EXCEL_EXPORT?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
