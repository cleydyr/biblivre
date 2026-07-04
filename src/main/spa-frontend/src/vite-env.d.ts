/* eslint-disable @typescript-eslint/no-unused-vars */
/// <reference types="vite/client" />

declare global {
  /** Set by Thymeleaf in spa.template from BIBLIVRE_FLAGSMITH_ENVIRONMENT_KEY */
  var __FLAGSMITH_ENVIRONMENT_KEY__: string | undefined
  /** Set by Thymeleaf in spa.template from FLAGSMITH_API_URL */
  var __FLAGSMITH_API_URL__: string | undefined

  interface GlobalThis {
    __FLAGSMITH_ENVIRONMENT_KEY__?: string
    __FLAGSMITH_API_URL__?: string
  }
}

interface ViteTypeOptions {
  // By adding this line, you can make the type of ImportMetaEnv strict
  // to disallow unknown keys.
  strictImportMetaEnv: unknown
}

interface ImportMetaEnv {
  /** Optional: local Vite dev only when not loading the app via Spring SPA HTML */
  readonly VITE_FLAGSMITH_ENVIRONMENT_KEY?: string
  readonly VITE_FLAGSMITH_API_URL?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

export {}
