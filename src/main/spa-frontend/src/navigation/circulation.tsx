import { FormattedMessage } from 'react-intl'
import { useLocation, useNavigate } from 'react-router-dom'

import { ACTIONS } from '../api-helpers/menu/constants'
import useCheckMenuPermission from '../api-helpers/menu/hooks'

import type { EuiSideNavItemType } from '@elastic/eui'

const useCirculationNavigation = (): EuiSideNavItemType<unknown> => {
  const navigate = useNavigate()
  const location = useLocation()

  const { data: isUserMenuEnabled } = useCheckMenuPermission(
    ACTIONS.CIRCULATION_USER,
  )
  const { data: isLendingMenuEnabled } = useCheckMenuPermission(
    ACTIONS.CIRCULATION_LENDING,
  )

  return {
    id: 'circulation',
    name: (
      <FormattedMessage
        defaultMessage='Circulação'
        id='app.sideNav.circulation'
      />
    ),
    items: [
      {
        id: 'user',
        name: (
          <FormattedMessage
            defaultMessage='Cadastro de usuários'
            id='app.sideNav.circulation.user'
          />
        ),
        onClick: () => {
          navigate('/spa/circulation_user')
        },
        isSelected: location.pathname === '/spa/circulation_user',
        disabled: !isUserMenuEnabled,
      },
      {
        id: 'lending',
        name: (
          <FormattedMessage
            defaultMessage='Empréstimo e renovação'
            id='app.sideNav.circulation.lending'
          />
        ),
        onClick: () => {
          navigate('/spa/circulation_lending')
        },
        isSelected: location.pathname === '/spa/circulation_lending',
        disabled: !isLendingMenuEnabled,
      },
      {
        id: 'return',
        name: (
          <FormattedMessage
            defaultMessage='Devolução'
            id='app.sideNav.circulation.return'
          />
        ),
        onClick: () => {
          navigate('/spa/circulation_return')
        },
        isSelected: location.pathname === '/spa/circulation_return',
        disabled: !isLendingMenuEnabled,
      },
    ],
  }
}

export default useCirculationNavigation
