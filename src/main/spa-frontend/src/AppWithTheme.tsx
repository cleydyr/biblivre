import { EuiProvider } from '@elastic/eui'
import { useState } from 'react'

import App from './App'
import ColorModeContext from './ColorModeContext'

import type { EuiThemeColorMode } from '@elastic/eui'

const AppWithTheme = () => {
  const [colorMode, setColorMode] = useState<EuiThemeColorMode>('light')

  return (
    <ColorModeContext.Provider value={{ colorMode, setColorMode }}>
      <EuiProvider colorMode={colorMode}>
        <App />
      </EuiProvider>
    </ColorModeContext.Provider>
  )
}

export default AppWithTheme
