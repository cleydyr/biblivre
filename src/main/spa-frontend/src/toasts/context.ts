import { createContext } from 'react'

import type { ToastContextValue } from './types'

export const ToastContext = createContext<ToastContextValue | null>(null)

export default ToastContext
