import { EuiButton } from '@elastic/eui'
import { Fragment, useState } from 'react'

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

  if (isError) {
    return <div>Error</div>
  }

  return (
    <Fragment>
      <EuiButton
        isLoading={isPending}
        onClick={() => {
          search()
        }}
      >
        Search
      </EuiButton>
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
