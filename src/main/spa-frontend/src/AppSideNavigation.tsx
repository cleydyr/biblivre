import { EuiFlexGroup, EuiFlexItem, EuiSideNav } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import ExperimentalAppBadge from './components/ExperimentalAppBadge'
import useNavigationItems from './navigation'

import type { EuiSideNavItemType } from '@elastic/eui'

const AppSideNavigation = () => {
  return (
    <EuiSideNav
      heading={
        <EuiFlexGroup alignItems='center' gutterSize='s' responsive={false}>
          <EuiFlexItem grow={false}>
            <FormattedMessage
              defaultMessage='Biblivre'
              id='app.sideNav.search.main'
            />
          </EuiFlexItem>
          <EuiFlexItem grow={false}>
            <ExperimentalAppBadge />
          </EuiFlexItem>
        </EuiFlexGroup>
      }
      items={filterEnabledItems(useNavigationItems())}
    />
  )
}

const filterEnabledItems = (items: EuiSideNavItemType<unknown>[]) => {
  return items.filter(
    (item) =>
      item.items === undefined ||
      item.items.some((subItem) => subItem.disabled !== true),
  )
}

export default AppSideNavigation
