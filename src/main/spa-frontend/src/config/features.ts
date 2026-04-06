import { useFlags } from '@flagsmith/flagsmith/react'

/** Boolean feature name in Flagsmith (must exist in the project/environment). */
export const SEARCH_EXCEL_EXPORT_FEATURE = 'search_excel_export' as const

export function useSearchExcelExportEnabled(): boolean {
  const flags = useFlags([SEARCH_EXCEL_EXPORT_FEATURE])

  return flags[SEARCH_EXCEL_EXPORT_FEATURE]?.enabled ?? false
}
