/// <reference types="vite/client" />

/** Set by Thymeleaf in spa.template from BIBLIVRE_FLAGSMITH_ENVIRONMENT_KEY */
declare var __FLAGSMITH_ENVIRONMENT_KEY__: string | undefined
/** Set by Thymeleaf in spa.template from FLAGSMITH_API_URL */
declare var __FLAGSMITH_API_URL__: string | undefined

interface ViteTypeOptions {
  // By adding this line, you can make the type of ImportMetaEnv strict
  // to disallow unknown keys.
  strictImportMetaEnv: unknown
}

interface ImportMetaEnv {
  readonly VITE_BIBLIVRE_ENDPOINT: string
  /** Optional: local Vite dev only when not loading the app via Spring SPA HTML */
  readonly VITE_FLAGSMITH_ENVIRONMENT_KEY?: string
  readonly VITE_FLAGSMITH_API_URL?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
