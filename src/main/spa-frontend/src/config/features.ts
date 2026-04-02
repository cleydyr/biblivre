/**
 * Build-time feature flags (Vite: set `VITE_*` in `.env` / `.env.local`).
 * Must match server-side `BIBLIVRE_FEATURE_SEARCH_EXCEL_EXPORT` when using export.
 */
export function isSearchExcelExportEnabled(): boolean {
  return import.meta.env.VITE_FEATURE_SEARCH_EXCEL_EXPORT === 'true'
}
