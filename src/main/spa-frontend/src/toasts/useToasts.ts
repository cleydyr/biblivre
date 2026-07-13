import { useContext } from 'react'

import ToastContext from './context'

import type { ToastContextValue } from './types'

export const useToasts = (): ToastContextValue => {
  const context = useContext(ToastContext)

  if (context === null) {
    throw new Error('useToasts must be used within a ToastProvider')
  }

  return context
}
