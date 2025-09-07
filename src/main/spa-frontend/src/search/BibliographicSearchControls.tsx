import { EuiFlexGroup, EuiSwitch } from '@elastic/eui'
import { useToggle } from '@uidotdev/usehooks'
import { FormattedMessage } from 'react-intl'

import SimpleBibliographicSearchControls from './SimpleBibliographicSearchControls'

import type { SearchQuery } from '../api-helpers/search/types'
import type { FC } from 'react'

type Props = {
  onQuerySubmited: (query?: SearchQuery) => void
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
      <SimpleBibliographicSearchControls
        isLoading={isLoading}
        onQuerySubmited={onQuerySubmited}
      />
    </EuiFlexGroup>
  )
}

export default BibliographicSearchControls
