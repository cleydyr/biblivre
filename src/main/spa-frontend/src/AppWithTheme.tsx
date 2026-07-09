import { EuiProvider } from '@elastic/eui'
import { useState } from 'react'

import { ToastProvider } from './toasts/ToastProvider'
import App from './App'

const AppWithTheme = () => {
  const [isDarkMode, setIsDarkMode] = useState(false)

  return (
    <EuiProvider colorMode={isDarkMode ? 'dark' : 'light'}>
      <ToastProvider>
        <App isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode} />
      </ToastProvider>
    </EuiProvider>
  )
}

export default AppWithTheme
