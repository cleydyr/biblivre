import {
  EuiButtonIcon,
  EuiFlexGroup,
  EuiHeader,
  EuiHeaderSection,
  EuiHeaderSectionItem,
} from '@elastic/eui'
import { useIntl } from 'react-intl'
import { Route, Routes } from 'react-router-dom'

import BibliographicSearchPage from './search/BibliographicSearchPage'

interface AppProps {
  isDarkMode: boolean
  setIsDarkMode: (isDarkMode: boolean) => void
}

const App = ({ isDarkMode, setIsDarkMode }: AppProps) => {
  const { formatMessage } = useIntl()
  return (
    <EuiFlexGroup direction='column' gutterSize='none'>
      <EuiHeader>
        <EuiHeaderSection side='right'>
          <EuiHeaderSectionItem>
            <EuiButtonIcon
              aria-label={formatMessage({
                defaultMessage: 'Alternar entre modo claro e escuro',
                id: 'app.dark_mode.button-description',
              })}
              iconType={isDarkMode ? 'sun' : 'moon'}
              onClick={() => setIsDarkMode(!isDarkMode)}
            />
          </EuiHeaderSectionItem>
        </EuiHeaderSection>
      </EuiHeader>
      <Routes>
        <Route element={<BibliographicSearchPage />} path='/search' />
      </Routes>
    </EuiFlexGroup>
  )
}

export default App
