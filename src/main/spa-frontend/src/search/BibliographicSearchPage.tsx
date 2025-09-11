import {
  EuiEmptyPrompt,
  EuiFlexGroup,
  EuiFlexItem,
  EuiPageTemplate,
  EuiPanel,
  EuiStat,
  type Pagination,
} from '@elastic/eui'
import { useState } from 'react'
import { FormattedMessage } from 'react-intl'

import BibliographicSearchControls from './BibliographicSearchControls'
import BibliographicSearchResultSort from './BibliographicSearchResultSort'
import BibliographicSearchResultsTable from './BibliographicSearchResultsTable'
import { usePaginatedSearch } from './hooks'

import type {
  BibliographicRecord,
  SuccessfulSearchResponse,
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
    <EuiPageTemplate grow={false} paddingSize='l' restrictWidth={920}>
      <EuiPageTemplate.Header
        pageTitle={
          <FormattedMessage
            defaultMessage='Pesquisa Bibliográfica'
            id='search.bibliographic.header.1'
          />
        }
      />

      <EuiPageTemplate.Section>
        <EuiFlexGroup direction='column'>
          <EuiPanel hasBorder paddingSize='l'>
            <BibliographicSearchControls
              isLoading={isSearchFetching}
              onQuerySubmited={(query) => {
                setTerms(query)
                setPage(0)
                setQuerySubmittedOnce(true)
              }}
            />
          </EuiPanel>
          {isSearchSuccess && searchResults.success === false && (
            <EuiEmptyPrompt
              body={
                <FormattedMessage
                  defaultMessage='A pesquisa não retornou nenhum resultado'
                  id='search.bibliographic.no-results'
                />
              }
              color='warning'
              iconType='warning'
            />
          )}
          {isSearchSuccess && searchResults.success && (
            <EuiFlexGroup direction='column'>
              <EuiFlexGroup alignItems='flexEnd' justifyContent='flexEnd'>
                <EuiFlexItem grow={false}>
                  <EuiStat
                    description={
                      <FormattedMessage
                        defaultMessage='Total de registros'
                        id='search.bibliographic.results'
                      />
                    }
                    title={searchResults.search.record_count}
                  />
                </EuiFlexItem>
                <EuiFlexItem>
                  <EuiFlexGroup alignItems='flexEnd' justifyContent='flexEnd'>
                    <BibliographicSearchResultSort
                      onSortChange={(sort: number) => {
                        setSort(sort)
                        setPage(0)
                      }}
                    />
                  </EuiFlexGroup>
                </EuiFlexItem>
              </EuiFlexGroup>
              <BibliographicSearchResultsTable
                isLoading={isSearchFetching}
                items={searchResults.search.data}
                pagination={getPagination(searchResults, page)}
                onChange={(criteria) => {
                  setPage(criteria.page?.index ?? 0)
                }}
                onSelectItems={setSelectedRecords}
              />
            </EuiFlexGroup>
          )}
        </EuiFlexGroup>
      </EuiPageTemplate.Section>
    </EuiPageTemplate>
  )
}

function getPagination(
  searchResults: SuccessfulSearchResponse,
  page: number
): Pagination | undefined {
  if (searchResults.search.page_count > 1) {
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
