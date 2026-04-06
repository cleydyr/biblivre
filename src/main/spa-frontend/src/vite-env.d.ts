/// <reference types="vite/client" />

interface Window {
  /** Set by Thymeleaf in spa.template from BIBLIVRE_FLAGSMITH_ENVIRONMENT_KEY */
  __FLAGSMITH_ENVIRONMENT_KEY__?: string
}

interface ViteTypeOptions {
  // By adding this line, you can make the type of ImportMetaEnv strict
  // to disallow unknown keys.
  strictImportMetaEnv: unknown
}

interface ImportMetaEnv {
  readonly VITE_BIBLIVRE_ENDPOINT: string
  /** Optional: local Vite dev only when not loading the app via Spring SPA HTML */
  readonly VITE_FLAGSMITH_ENVIRONMENT_KEY?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
