import { EuiSideNav, type EuiSideNavItemType } from '@elastic/eui'
import { useId } from 'react'
import { FormattedMessage } from 'react-intl'
import { useLocation, useNavigate } from 'react-router-dom'

const AppSideNavigation = () => {
  const navigate = useNavigate()
  const location = useLocation()

  const items: EuiSideNavItemType<unknown>[] = [
    {
      id: useId(),
      name: (
        <FormattedMessage
          defaultMessage='Biblivre'
          id='app.sideNav.search.main'
        />
      ),
      items: [
        {
          id: useId(),
          name: (
            <FormattedMessage
              defaultMessage='Pesquisa'
              id='app.sideNav.search'
            />
          ),
          items: [
            {
              id: useId(),
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
        },
      ],
    },
  ]

  return <EuiSideNav items={items} />
}

export default AppSideNavigation
