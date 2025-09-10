import { EuiFlexGroup, EuiSwitch } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import useToggle from '../hooks/useToggle'

import AdvancedBibliographicSearchControls from './advanced/AdvancedBibliographicSearchControls'
import SimpleBibliographicSearchControls from './SimpleBibliographicSearchControls'

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
    <EuiFlexGroup>
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
