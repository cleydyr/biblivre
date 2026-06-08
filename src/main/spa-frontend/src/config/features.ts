import { useFlags } from '@flagsmith/flagsmith/react'

/** Boolean feature name in Flagsmith (must exist in the project/environment). */
export const SEARCH_EXCEL_EXPORT_FEATURE = 'search_excel_export' as const

export function useFeatureFlag(featureName: string): boolean {
  const flags = useFlags([featureName])

  return flags[featureName]?.enabled ?? false
}
