import { EuiPageTemplate, useEuiTheme } from '@elastic/eui'
import { Route, type RouteObject, Routes } from 'react-router-dom'

import ReportApp from './administration/reports/ReportApp'
import { ACTIONS } from './api-helpers/menu/constants'
import useCheckMenuPermission from './api-helpers/menu/hooks'
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

  const routes = useAppRoutes()

  return (
    <EuiPageTemplate paddingSize='xl' restrictWidth={euiTheme.breakpoint.xl}>
      <EuiPageTemplate.Sidebar>
        <AppSideNavigation />
      </EuiPageTemplate.Sidebar>
      <AppHeader isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode} />
      <Routes>
        <Route path='/spa'>
          {routes.map((route) => (
            <Route key={route.path} element={route.element} path={route.path} />
          ))}
        </Route>
      </Routes>
    </EuiPageTemplate>
  )
}

const useAppRoutes = (): RouteObject[] => [
  {
    path: 'search',
    element: <BibliographicSearchPage />,
  },
  {
    path: 'record/:recordId',
    element: <BibliographicRecordPage />,
  },
  useCheckedMenuRoute(
    {
      path: 'reports',
      element: <ReportApp />,
    },
    ACTIONS.ADMINISTRATION_CUSTOM_REPORTS,
  ),
]

function useCheckedMenuRoute(
  route: RouteObject,
  action: (typeof ACTIONS)[keyof typeof ACTIONS],
): RouteObject {
  const { data: isMenuEnabled } = useCheckMenuPermission(action)

  if (!isMenuEnabled) {
    return {
      ...route,
      element: null, // TODO: add a component to show that the route is disabled or not found
    }
  }

  return route
}

export default App
