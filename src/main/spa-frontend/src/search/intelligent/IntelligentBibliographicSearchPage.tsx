import { EuiFlexGroup, EuiPanel } from '@elastic/eui'
import { useState } from 'react'
import { FormattedMessage } from 'react-intl'

import PageTemplate from '../../components/PageTemplate'
import BibliographicSearchResults from '../BibliographicSearchResults'
import { usePaginatedSearch } from '../hooks'

import IntelligentBibliographicSearchControls from './IntelligentBibliographicSearchControls'

import type {
  BibliographicMaterial,
  EncodedQueryField,
  SearchQueryTerms,
} from '../../api-helpers/search/types'

const IntelligentBibliographicSearchPage = () => {
  const [terms, setTerms] = useState<SearchQueryTerms | undefined>()
  const [page, setPage] = useState(0)
  const [sort, setSort] = useState<EncodedQueryField | undefined>(undefined)
  const [materialType, setMaterialType] = useState<BibliographicMaterial>('all')
  const [isQuerySubmittedOnce, setQuerySubmittedOnce] = useState(false)

  const {
    data: searchResults,
    isSuccess: isSearchSuccess,
    isError: isSearchError,
    isFetching: isSearchFetching,
  } = usePaginatedSearch(terms, page, materialType, sort, 'intelligent', {
    enabled: isQuerySubmittedOnce,
  })

  return (
    <PageTemplate
      pageTitle={
        <FormattedMessage
          defaultMessage='Pesquisa Inteligente'
          id='search.bibliographic.intelligent.header.1'
        />
      }
    >
      <EuiFlexGroup direction='column'>
        <EuiPanel hasBorder paddingSize='l'>
          <IntelligentBibliographicSearchControls
            isLoading={isSearchFetching}
            onQuerySubmited={(queryMaterialType, newTerms) => {
              setTerms(newTerms)
              setPage(0)
              setMaterialType(queryMaterialType)
              setQuerySubmittedOnce(true)
            }}
          />
        </EuiPanel>
        <BibliographicSearchResults
          isSearchError={isSearchError}
          isSearchFetching={isSearchFetching}
          isSearchSuccess={isSearchSuccess}
          materialType={materialType}
          searchMode='intelligent'
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

export default IntelligentBibliographicSearchPage
