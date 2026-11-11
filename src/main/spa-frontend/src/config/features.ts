import { useFlags } from '@flagsmith/flagsmith/react'

/** Boolean feature name in Flagsmith (must exist in the project/environment). */
export const SEARCH_EXCEL_EXPORT_FEATURE = 'search_excel_export' as const
export const SEARCH_INTELLIGENT_FEATURE = 'search_intelligent' as const
export const CIRCULATION_LENDING_PROTOTYPE_FEATURE =
  'circulation_lending_prototype' as const

/**
 * Dev-only overrides for working without a Flagsmith environment: features
 * listed (comma-separated) in `VITE_FEATURE_FLAGS`, e.g. in `.env.local`, are
 * forced on. Ignored in production builds.
 */
export function parseFeatureOverrides(value: string | undefined): Set<string> {
  return new Set(
    (value ?? '')
      .split(',')
      .map((name) => name.trim())
      .filter(Boolean),
  )
}

const LOCAL_FEATURE_OVERRIDES = import.meta.env.DEV
  ? parseFeatureOverrides(import.meta.env.VITE_FEATURE_FLAGS)
  : new Set<string>()

export function useFeatureFlag(featureName: string): boolean {
  const flags = useFlags([featureName])

  return LOCAL_FEATURE_OVERRIDES.has(featureName) || flags[featureName].enabled
}
