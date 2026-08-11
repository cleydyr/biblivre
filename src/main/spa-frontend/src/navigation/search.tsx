import { EuiBadge, EuiFlexGroup, EuiFlexItem } from '@elastic/eui'
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
                <EuiFlexGroup
                  alignItems='center'
                  gutterSize='s'
                  responsive={false}
                >
                  <EuiFlexItem grow={false}>
                    <FormattedMessage
                      defaultMessage='Pesquisa Inteligente'
                      id='app.sideNav.search.intelligent'
                    />
                  </EuiFlexItem>
                  <EuiFlexItem grow={false}>
                    <EuiBadge color='accent'>
                      <FormattedMessage
                        defaultMessage='Novo'
                        id='app.sideNav.search.intelligent.badge.new'
                      />
                    </EuiBadge>
                  </EuiFlexItem>
                </EuiFlexGroup>
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
