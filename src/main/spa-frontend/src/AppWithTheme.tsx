import { EuiProvider } from '@elastic/eui'
import { useState } from 'react'

import App from './App'

const AppWithTheme = () => {
  const [isDarkMode, setIsDarkMode] = useState(false)

  return (
    <EuiProvider colorMode={isDarkMode ? 'dark' : 'light'}>
      <App isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode} />
    </EuiProvider>
  )
}

export default AppWithTheme
