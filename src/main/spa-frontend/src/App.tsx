import { EuiPageTemplate, useEuiTheme } from '@elastic/eui'
import { Route, Routes } from 'react-router-dom'

import ReportApp from './administration/reports/ReportApp'
import BibliographicRecordPage from './open_record/BibliographicRecordPage'
import BibliographicSearchPage from './search/BibliographicSearchPage'
import AppHeader from './AppHeader'
import AppSideNavigation from './AppSideNavigation'

interface AppProps {
  isDarkMode: boolean
  setIsDarkMode: (isDarkMode: boolean) => void
}

const App = ({ isDarkMode, setIsDarkMode }: AppProps) => {
  const { euiTheme } = useEuiTheme()

  return (
    <EuiPageTemplate paddingSize='xl' restrictWidth={euiTheme.breakpoint.xl}>
      <EuiPageTemplate.Sidebar>
        <AppSideNavigation />
      </EuiPageTemplate.Sidebar>
      <AppHeader isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode} />
      <Routes>
        <Route path='/spa'>
          <Route element={<BibliographicSearchPage />} path='search' />
          <Route
            element={<BibliographicRecordPage />}
            path='record/:recordId'
          />
          <Route element={<ReportApp />} path='reports' />
        </Route>
      </Routes>
    </EuiPageTemplate>
  )
}

export default App
