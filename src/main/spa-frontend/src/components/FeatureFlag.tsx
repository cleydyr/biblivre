import { useFeatureFlag } from '../config/features'

import type { ReactNode } from 'react'

type FeatureFlagProps = {
  name: string
  children: ReactNode
  fallback?: ReactNode
}

const FeatureFlag = ({ name, children, fallback = null }: FeatureFlagProps) => {
  const enabled = useFeatureFlag(name)

  if (!enabled) {
    return fallback
  }

  return children
}

export default FeatureFlag
