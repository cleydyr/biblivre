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
      items: filterEnabledItems(useNavigationItems()),
    },
  ]

  return <EuiSideNav items={items} />
}

const filterEnabledItems = (items: EuiSideNavItemType<unknown>[]) => {
  return items.filter(
    (item) =>
      item.items === undefined ||
      item.items.some((subItem) => subItem.disabled !== true),
  )
}

export default AppSideNavigation
