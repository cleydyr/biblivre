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
import messages from './messages'

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
              aria-label={formatMessage(messages.darkButtonDescription)}
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
