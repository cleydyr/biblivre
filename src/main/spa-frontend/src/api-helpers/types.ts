import type { MODULES } from './constants'

export type LegacyModule = (typeof MODULES)[keyof typeof MODULES]
