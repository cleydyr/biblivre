import { EuiGlobalToastList } from '@elastic/eui'
import { useCallback, useMemo, useState } from 'react'

import ToastContext from './context'

import type { Toast } from '@elastic/eui/src/components/toast/global_toast_list'
import type { ReactNode } from 'react'

type ToastProviderProps = {
  children: ReactNode
  toastLifeTimeMs?: number
}

export const ToastProvider = ({
  children,
  toastLifeTimeMs = 5000,
}: ToastProviderProps) => {
  const [toasts, setToasts] = useState<Toast[]>([])

  const removeToast = useCallback((toast: Toast) => {
    setToasts((currentToasts) =>
      currentToasts.filter((currentToast) => currentToast.id !== toast.id),
    )
  }, [])

  const showToasts = useCallback((nextToasts: Toast[]) => {
    setToasts(nextToasts)
  }, [])

  const showToast = useCallback((toast: Toast) => {
    setToasts([toast])
  }, [])

  const value = useMemo(
    () => ({
      showToast,
      showToasts,
      removeToast,
    }),
    [showToast, showToasts, removeToast],
  )

  return (
    <ToastContext.Provider value={value}>
      {children}
      <EuiGlobalToastList
        dismissToast={removeToast}
        toastLifeTimeMs={toastLifeTimeMs}
        toasts={toasts}
      />
    </ToastContext.Provider>
  )
}
