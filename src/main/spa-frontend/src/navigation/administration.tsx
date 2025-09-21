import { FormattedMessage } from 'react-intl'
import { useLocation, useNavigate } from 'react-router-dom'

const useAdministrationNavigation = () => {
  const navigate = useNavigate()
  const location = useLocation()

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
      },
    ],
  }
}

export default useAdministrationNavigation
