import { EuiButton, EuiFlexGroup, EuiSwitch } from '@elastic/eui'
import { Fragment, useState } from 'react'
import BibliographicSearchResultsTable from './BibliographicSearchResultsTable'
import { useSearchCatalographic } from './hooks'
import { useToggle } from '@uidotdev/usehooks'

import type { BibliographicRecord } from '../api-helpers/search/response-types'
import { FormattedMessage } from 'react-intl'

const ConnectedBibliographicSearchPage = () => {
  const {
    mutate: search,
    isSuccess,
    isPending,
    isError,
    data: searchResults,
  } = useSearchCatalographic()

  const [_, setSelectedRecords] = useState<BibliographicRecord[]>([])

  const [isAdvancedSearch, toggleAdvancedSearch] = useToggle(false)

  if (isError) {
    return <div>Error</div>
  }

  return (
    <Fragment>
      <EuiFlexGroup>
        <EuiButton
          isLoading={isPending}
          onClick={() => {
            search()
          }}
        >
          <FormattedMessage
            id='search.bibliographic.search_all'
            defaultMessage='Listar todos'
          />
        </EuiButton>
        <EuiSwitch
          checked={isAdvancedSearch}
          onChange={() => toggleAdvancedSearch()}
          label={
            <FormattedMessage
              id='search.bibliographic.advanced_search'
              defaultMessage='Pesquisa avançada'
            />
          }
        />
      </EuiFlexGroup>
      {isSuccess && (
        <BibliographicSearchResultsTable
          isPending={isPending}
          items={searchResults.success ? searchResults.search.data : []}
          onSelectItems={setSelectedRecords}
        />
      )}
    </Fragment>
  )
}

export default ConnectedBibliographicSearchPage
