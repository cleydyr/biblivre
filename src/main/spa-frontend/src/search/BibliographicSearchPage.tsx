import { EuiButton, EuiFlexGroup, EuiSwitch } from '@elastic/eui'
import { useToggle } from '@uidotdev/usehooks'
import { Fragment, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import BibliographicSearchResultsTable from './BibliographicSearchResultsTable'
import { useSearchCatalographic } from './hooks'

import type { BibliographicRecord } from '../api-helpers/search/response-types'

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
            defaultMessage='Listar todos'
            id='search.bibliographic.search_all'
          />
        </EuiButton>
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
