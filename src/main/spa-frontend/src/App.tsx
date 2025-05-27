import { EuiFlexGroup } from '@elastic/eui'
import { Route, Routes } from 'react-router-dom'

import BibliographicRecordPage from './open_record/BibliographicRecordPage'
import BibliographicSearchPage from './search/BibliographicSearchPage'
import AppHeader from './AppHeader'

interface AppProps {
  isDarkMode: boolean
  setIsDarkMode: (isDarkMode: boolean) => void
}

const App = ({ isDarkMode, setIsDarkMode }: AppProps) => {
  return (
    <EuiFlexGroup direction='column' gutterSize='none'>
      <AppHeader isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode} />
      <Routes>
        <Route path='/spa'>
          <Route element={<BibliographicSearchPage />} path='search' />
          <Route
            element={<BibliographicRecordPage />}
            path='record/:recordId'
          />
        </Route>
      </Routes>
    </EuiFlexGroup>
  )
}

export default App
