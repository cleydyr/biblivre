import { EuiSideNav, type EuiSideNavItemType } from '@elastic/eui'
import { useId } from 'react'
import { FormattedMessage } from 'react-intl'

import useNavigationItems from './navigation'

const AppSideNavigation = () => {
  const items: EuiSideNavItemType<unknown>[] = [
    {
      id: useId(),
      name: (
        <FormattedMessage
          defaultMessage='Biblivre'
          id='app.sideNav.search.main'
        />
      ),
      items: useNavigationItems(),
    },
  ]

  return <EuiSideNav items={items} />
}

export default AppSideNavigation
