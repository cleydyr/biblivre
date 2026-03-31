import { FormattedMessage } from 'react-intl'
import { useLocation, useNavigate } from 'react-router-dom'

const useSearchNavigation = () => {
  const navigate = useNavigate()
  const location = useLocation()

  return {
    id: 'search',
    name: (
      <FormattedMessage defaultMessage='Pesquisa' id='app.sideNav.search' />
    ),
    items: [
      {
        id: 'search-bibliographic',
        name: (
          <FormattedMessage
            defaultMessage='Pesquisa Bibliográfica'
            id='app.sideNav.search.bibliographic'
          />
        ),
        onClick: () => {
          navigate('/spa/search')
        },
        isSelected: location.pathname === '/spa/search',
      },
    ],
  }
}

export default useSearchNavigation
