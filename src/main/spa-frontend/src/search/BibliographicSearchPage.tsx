import { EuiFlexGroup, EuiPanel } from '@elastic/eui'
import { useState } from 'react'
import { FormattedMessage } from 'react-intl'

import PageTemplate from '../components/PageTemplate'

import BibliographicSearchControls from './BibliographicSearchControls'
import BibliographicSearchResults from './BibliographicSearchResults'
import { usePaginatedSearch } from './hooks'

import type {
  BibliographicMaterial,
  EncodedQueryField,
  SearchMode,
  SearchQueryTerms,
} from '../api-helpers/search/types'

const BibliographicSearchPage = () => {
  const [terms, setTerms] = useState<SearchQueryTerms | undefined>()
  const [page, setPage] = useState(0)
  const [sort, setSort] = useState<EncodedQueryField | undefined>(undefined)
  const [materialType, setMaterialType] = useState<BibliographicMaterial>('all')
  const [searchMode, setSearchMode] = useState<SearchMode>('simple')
  const [isQuerySubmittedOnce, setQuerySubmittedOnce] = useState(false)

  const {
    data: searchResults,
    isSuccess: isSearchSuccess,
    isError: isSearchError,
    isFetching: isSearchFetching,
  } = usePaginatedSearch(terms, page, materialType, sort, searchMode, {
    enabled: isQuerySubmittedOnce,
  })

  return (
    <PageTemplate
      pageTitle={
        <FormattedMessage
          defaultMessage='Pesquisa Bibliográfica'
          id='search.bibliographic.header.1'
        />
      }
    >
      <EuiFlexGroup direction='column'>
        <EuiPanel hasBorder paddingSize='l'>
          <BibliographicSearchControls
            isLoading={isSearchFetching}
            onQuerySubmited={(queryMaterialType, newTerms, mode) => {
              setTerms(newTerms)
              setPage(0)
              setMaterialType(queryMaterialType)
              setSearchMode(mode)
              setQuerySubmittedOnce(true)
            }}
          />
        </EuiPanel>
        <BibliographicSearchResults
          isSearchError={isSearchError}
          isSearchFetching={isSearchFetching}
          isSearchSuccess={isSearchSuccess}
          materialType={materialType}
          searchMode={searchMode}
          searchResults={searchResults}
          sort={sort}
          terms={terms}
          onPageChange={setPage}
          onSortChange={(newSort) => {
            setSort(newSort)
            setPage(0)
          }}
        />
      </EuiFlexGroup>
    </PageTemplate>
  )
}

export default BibliographicSearchPage
