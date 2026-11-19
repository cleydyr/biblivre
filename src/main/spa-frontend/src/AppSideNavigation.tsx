import {
  EuiBadge,
  EuiFlexGroup,
  EuiFlexItem,
  EuiScreenReaderOnly,
  EuiSideNav,
  useEuiTheme,
} from '@elastic/eui'
import { css } from '@emotion/react'
import { Fragment, useId } from 'react'
import { FormattedMessage } from 'react-intl'

import ExperimentalAppBadge from './components/ExperimentalAppBadge'
import { SEARCH_INTELLIGENT_FEATURE, useFeatureFlag } from './config/features'
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
    <Fragment>
      <SideNavWidthGauge />
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
    </Fragment>
  )
}

// Keeps the column as wide as the longest label while a section is collapsed.
// EUI unmounts closed items, so max-content would otherwise shrink and shift
// the page when Pesquisa opens or closes.
const SideNavWidthGauge = () => {
  const isIntelligentSearchEnabled = useFeatureFlag(SEARCH_INTELLIGENT_FEATURE)
  const styles = useSideNavWidthGaugeStyles()

  if (!isIntelligentSearchEnabled) {
    return null
  }

  return (
    <div aria-hidden css={styles.gauge}>
      <span css={styles.row}>
        <FormattedMessage
          defaultMessage='Pesquisa Inteligente'
          id='app.sideNav.search.intelligent'
        />
        <EuiBadge color='accent'>
          <FormattedMessage
            defaultMessage='Novo'
            id='app.sideNav.search.intelligent.badge.new'
          />
        </EuiBadge>
      </span>
    </div>
  )
}

const useSideNavWidthGaugeStyles = () => {
  const { euiTheme } = useEuiTheme()
  const labelFontSize = `${euiTheme.base * euiTheme.font.scale.s}px`

  return {
    gauge: css`
      overflow: hidden;
      block-size: 0;
      inline-size: max-content;
      margin-inline-start: ${euiTheme.size.s};
      padding-inline: ${euiTheme.size.s};
      visibility: hidden;
      pointer-events: none;
    `,
    row: css`
      display: flex;
      align-items: center;
      gap: ${euiTheme.size.s};
      font-size: ${labelFontSize};
      font-weight: ${euiTheme.font.weight.bold};
      white-space: nowrap;
    `,
  }
}

const filterEnabledItems = (items: EuiSideNavItemType<unknown>[]) => {
  return items.filter(
    (item) =>
      item.items === undefined ||
      item.items.some((subItem) => subItem.disabled !== true),
  )
}

export default AppSideNavigation
