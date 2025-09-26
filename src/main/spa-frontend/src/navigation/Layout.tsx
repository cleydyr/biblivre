import { EuiPageTemplate, useEuiTheme } from '@elastic/eui'
import { Outlet } from 'react-router'

import AppHeader from '../AppHeader'
import AppSideNavigation from '../AppSideNavigation'

const Layout = () => {
  const { euiTheme } = useEuiTheme()

  return (
    <EuiPageTemplate paddingSize='xl' restrictWidth={euiTheme.breakpoint.xl}>
      <EuiPageTemplate.Sidebar>
        <AppSideNavigation />
      </EuiPageTemplate.Sidebar>
      <AppHeader />
      <EuiPageTemplate.Section>
        <Outlet />
      </EuiPageTemplate.Section>
    </EuiPageTemplate>
  )
}

export default Layout
