import { Route, type RouteObject, Routes } from 'react-router-dom'

import ReportApp from './administration/reports/ReportApp'
import { ACTIONS } from './api-helpers/menu/constants'
import useCheckMenuPermission from './api-helpers/menu/hooks'
import Layout from './navigation/Layout'
import BibliographicRecordPage from './open_record/BibliographicRecordPage'
import BibliographicSearchPage from './search/BibliographicSearchPage'

const App = () => {
  const routes = useAppRoutes()

  return (
    <Routes>
      <Route element={<Layout />} path='/spa'>
        {routes.map((route) => (
          <Route key={route.path} element={route.element} path={route.path} />
        ))}
      </Route>
    </Routes>
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
