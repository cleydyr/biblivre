import { EuiProvider } from '@elastic/eui'
import { ErrorBoundary } from '@sentry/react'
import { useState } from 'react'

import AppErrorFallback from './monitoring/AppErrorFallback'
import { ToastProvider } from './toasts/ToastProvider'
import App from './App'

const AppWithTheme = () => {
  const [isDarkMode, setIsDarkMode] = useState(false)

  return (
    <EuiProvider colorMode={isDarkMode ? 'dark' : 'light'}>
      <ErrorBoundary fallback={<AppErrorFallback />}>
        <ToastProvider>
          <App isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode} />
        </ToastProvider>
      </ErrorBoundary>
    </EuiProvider>
  )
}

export default AppWithTheme
