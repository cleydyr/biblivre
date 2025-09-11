import { EuiFlexGroup, EuiSwitch, EuiTitle } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import useToggle from '../hooks/useToggle'

import AdvancedBibliographicSearchControls from './advanced/AdvancedBibliographicSearchControls'
import SimpleBibliographicSearchControls from './simple/SimpleBibliographicSearchControls'

import type { FC } from 'react'

import type { SearchQueryTerms } from '../api-helpers/search/types'

type Props = {
  onQuerySubmited: (terms?: SearchQueryTerms) => void
  isLoading: boolean
}

const BibliographicSearchControls: FC<Props> = ({
  onQuerySubmited,
  isLoading,
}) => {
  const [isAdvancedSearch, toggleAdvancedSearch] = useToggle(false)

  return (
    <EuiFlexGroup direction='column'>
      <EuiFlexGroup alignItems='center' justifyContent='spaceBetween'>
        <EuiTitle size='s'>
          <h2>
            {isAdvancedSearch ? (
              <FormattedMessage
                defaultMessage='Pesquisa Bibliográfica Avançada'
                id='search.bibliographic.header.2.advanced'
              />
            ) : (
              <FormattedMessage
                defaultMessage='Pesquisa Bibliográfica'
                id='search.bibliographic.header.2.simple'
              />
            )}
          </h2>
        </EuiTitle>
        <EuiSwitch
          checked={isAdvancedSearch}
          label={
            <FormattedMessage
              defaultMessage='Pesquisa avançada'
              id='search.bibliographic.advanced_search'
            />
          }
          onChange={() => toggleAdvancedSearch()}
        />
      </EuiFlexGroup>
      {isAdvancedSearch ? (
        <AdvancedBibliographicSearchControls
          isLoading={isLoading}
          onQuerySubmited={onQuerySubmited}
        />
      ) : (
        <SimpleBibliographicSearchControls
          isLoading={isLoading}
          onQuerySubmited={onQuerySubmited}
        />
      )}
    </EuiFlexGroup>
  )
}

export default BibliographicSearchControls
