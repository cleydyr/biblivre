import { createContext } from 'react'

import type { EuiThemeColorMode } from '@elastic/eui'

type ColorModeContextType = {
  colorMode: EuiThemeColorMode
  setColorMode: (colorMode: EuiThemeColorMode) => void
}
const ColorModeContext = createContext<ColorModeContextType>({
  colorMode: 'light',
  setColorMode: () => {},
})

export default ColorModeContext
