import { FormattedMessage } from 'react-intl'
import { useLocation, useNavigate } from 'react-router-dom'

import { SEARCH_INTELLIGENT_FEATURE, useFeatureFlag } from '../config/features'

const useSearchNavigation = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const isIntelligentSearchEnabled = useFeatureFlag(SEARCH_INTELLIGENT_FEATURE)

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
      ...(isIntelligentSearchEnabled
        ? [
            {
              id: 'search-intelligent',
              name: (
                <FormattedMessage
                  defaultMessage='Pesquisa Inteligente'
                  id='app.sideNav.search.intelligent'
                />
              ),
              onClick: () => {
                navigate('/spa/search/intelligent')
              },
              isSelected: location.pathname === '/spa/search/intelligent',
            },
          ]
        : []),
    ],
  }
}

export default useSearchNavigation
