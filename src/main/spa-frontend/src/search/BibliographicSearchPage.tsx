import { Fragment, useState } from 'react'

import BibliographicSearchControls from './BibliographicSearchControls'
import BibliographicSearchResultsTable from './BibliographicSearchResultsTable'
import { usePaginatedSearch } from './hooks'

import type { Pagination } from '@elastic/eui'

import type {
  BibliographicRecord,
  SearchResponse,
} from '../api-helpers/search/response-types'
import type { SearchQuery } from '../api-helpers/search/types'

const BibliographicSearchPage = () => {
  const [query, setQuery] = useState<SearchQuery | undefined>()

  const [page, setPage] = useState<number>(0)

  const [isQuerySubmittedOnce, setQuerySubmittedOnce] = useState<boolean>(false)

  const {
    data: searchResults,
    isSuccess: isSearchSuccess,
    isError: isSearchError,
    isFetching: isSearchFetching,
  } = usePaginatedSearch(query, page, {
    enabled: isQuerySubmittedOnce,
  })

  const [_, setSelectedRecords] = useState<BibliographicRecord[]>([])

  if (isSearchError) {
    return <div>Error</div>
  }

  return (
    <Fragment>
      <BibliographicSearchControls
        isLoading={isSearchFetching}
        onQuerySubmited={(query) => {
          setQuery(query)
          setPage(0)
          setQuerySubmittedOnce(true)
        }}
      />
      {isSearchSuccess && (
        <BibliographicSearchResultsTable
          isLoading={isSearchFetching}
          items={searchResults.success ? searchResults.search.data : []}
          pagination={getPagination(searchResults, page)}
          onChange={(criteria) => {
            setPage(criteria.page?.index ?? 0)
          }}
          onSelectItems={setSelectedRecords}
        />
      )}
    </Fragment>
  )
}

function getPagination(
  searchResults: SearchResponse,
  page: number
): Pagination | undefined {
  if (searchResults.success) {
    return {
      pageIndex: page,
      totalItemCount: searchResults.search.record_count,
      pageSize: searchResults.search.records_per_page,
      showPerPageOptions: false,
    }
  }

  return undefined
}

export default BibliographicSearchPage
