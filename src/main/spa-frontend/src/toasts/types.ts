import type { EuiGlobalToastListProps } from '@elastic/eui'

type Toast = Exclude<EuiGlobalToastListProps['toasts'], undefined>[number]

export type ToastContextValue = {
  showToast: (toast: Toast) => void
  showToasts: (toasts: Toast[]) => void
  removeToast: (toast: Toast) => void
}
