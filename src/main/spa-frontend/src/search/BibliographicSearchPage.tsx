import { Fragment, useState } from 'react'

import BibliographicSearchControls from './BibliographicSearchControls'
import BibliographicSearchResultSort from './BibliographicSearchResultSort'
import BibliographicSearchResultsTable from './BibliographicSearchResultsTable'
import { usePaginatedSearch } from './hooks'

import type { Pagination } from '@elastic/eui'

import type {
  BibliographicRecord,
  SearchResponse,
} from '../api-helpers/search/response-types'
import type { SearchQueryTerms } from '../api-helpers/search/types'

const BibliographicSearchPage = () => {
  const [terms, setTerms] = useState<SearchQueryTerms | undefined>()

  const [page, setPage] = useState<number>(0)

  const [sort, setSort] = useState<number | undefined>(undefined)

  const [isQuerySubmittedOnce, setQuerySubmittedOnce] = useState<boolean>(false)

  const {
    data: searchResults,
    isSuccess: isSearchSuccess,
    isError: isSearchError,
    isFetching: isSearchFetching,
  } = usePaginatedSearch(terms, page, sort, {
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
          setTerms(query)
          setPage(0)
          setQuerySubmittedOnce(true)
        }}
      />
      {isSearchSuccess && (
        <Fragment>
          <BibliographicSearchResultSort
            onSortChange={(sort: number) => {
              setSort(sort)
              setPage(0)
            }}
          />
          <BibliographicSearchResultsTable
            isLoading={isSearchFetching}
            items={searchResults.success ? searchResults.search.data : []}
            pagination={getPagination(searchResults, page)}
            onChange={(criteria) => {
              setPage(criteria.page?.index ?? 0)
            }}
            onSelectItems={setSelectedRecords}
          />
        </Fragment>
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
