import {
  EuiFlexGroup,
  EuiFlexItem,
  EuiScreenReaderOnly,
  EuiSideNav,
} from '@elastic/eui'
import { css } from '@emotion/react'
import { useId } from 'react'
import { FormattedMessage } from 'react-intl'

import ExperimentalAppBadge from './components/ExperimentalAppBadge'
import useNavigationItems from './navigation'

import type { EuiSideNavItemType } from '@elastic/eui'

const hiddenRootItemStyles = css`
  & > .euiSideNavItemButton {
    display: none;
  }
`

const AppSideNavigation = () => {
  const rootItemId = useId()

  const items: EuiSideNavItemType<unknown>[] = [
    {
      id: rootItemId,
      css: hiddenRootItemStyles,
      name: (
        <EuiScreenReaderOnly>
          <span>
            <FormattedMessage
              defaultMessage='Biblivre'
              id='app.sideNav.search.main'
            />
          </span>
        </EuiScreenReaderOnly>
      ),
      items: filterEnabledItems(useNavigationItems()),
    },
  ]

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
      items={items}
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
