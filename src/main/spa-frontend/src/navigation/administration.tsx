import { FormattedMessage } from 'react-intl'
import { useLocation, useNavigate } from 'react-router-dom'

import { ACTIONS } from '../api-helpers/menu/constants'
import useCheckMenuPermission from '../api-helpers/menu/hooks'

import type { EuiSideNavItemType } from '@elastic/eui'

const useAdministrationNavigation = (): EuiSideNavItemType<unknown> => {
  const navigate = useNavigate()
  const location = useLocation()

  const { data: isMenuEnabled } = useCheckMenuPermission(
    ACTIONS.ADMINISTRATION_CUSTOM_REPORTS,
  )

  return {
    id: 'administration',
    name: (
      <FormattedMessage
        defaultMessage='Administração'
        id='app.sideNav.administration'
      />
    ),
    items: [
      {
        id: 'custom-reports',
        name: (
          <FormattedMessage
            defaultMessage='Relatórios personalizados'
            id='app.sideNav.administration.custom_reports'
          />
        ),
        onClick: () => {
          navigate('/spa/reports')
        },
        isSelected: location.pathname === '/spa/reports',
        disabled: !isMenuEnabled,
      },
    ],
  }
}

export default useAdministrationNavigation
