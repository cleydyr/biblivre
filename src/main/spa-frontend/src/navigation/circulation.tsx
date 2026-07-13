import { FormattedMessage } from 'react-intl'
import { useLocation, useNavigate } from 'react-router-dom'

import { ACTIONS } from '../api-helpers/menu/constants'
import useCheckMenuPermission from '../api-helpers/menu/hooks'

import type { EuiSideNavItemType } from '@elastic/eui'

const useCirculationNavigation = (): EuiSideNavItemType<unknown> => {
  const navigate = useNavigate()
  const location = useLocation()

  const { data: isMenuEnabled } = useCheckMenuPermission(
    ACTIONS.CIRCULATION_USER,
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
        disabled: !isMenuEnabled,
      },
    ],
  }
}

export default useCirculationNavigation
