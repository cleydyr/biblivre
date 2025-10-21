import {
  EuiEmptyPrompt,
  EuiFlexGroup,
  EuiFlexItem,
  EuiPageTemplate,
  EuiPanel,
  EuiStat,
  useEuiTheme,
} from '@elastic/eui'
import { useState } from 'react'
import { FormattedMessage } from 'react-intl'

import BibliographicSearchControls from './BibliographicSearchControls'
import BibliographicSearchResultSort from './BibliographicSearchResultSort'
import BibliographicSearchResultsTable from './BibliographicSearchResultsTable'
import { usePaginatedSearch } from './hooks'
import BibliographicRecordFlyout from './RecordFlyout'

import type { Pagination } from '@elastic/eui'

import type { SuccessfulSearchResponse } from '../api-helpers/search/response-types'
import type {
  BibliographicMaterial,
  EncodedQueryField,
  SearchQueryTerms,
} from '../api-helpers/search/types'

const BibliographicSearchPage = () => {
  const { euiTheme } = useEuiTheme()

  const [terms, setTerms] = useState<SearchQueryTerms | undefined>()

  const [page, setPage] = useState<number>(0)

  const [sort, setSort] = useState<EncodedQueryField | undefined>(undefined)

  const [materialType, setMaterialType] = useState<BibliographicMaterial>('all')

  const [isQuerySubmittedOnce, setQuerySubmittedOnce] = useState<boolean>(false)

  const {
    data: searchResults,
    isSuccess: isSearchSuccess,
    isError: isSearchError,
    isFetching: isSearchFetching,
  } = usePaginatedSearch(terms, page, materialType, sort, {
    enabled: isQuerySubmittedOnce,
  })

  const [recordIdForFlyout, setRecordIdForFlyout] = useState<
    number | undefined
  >(undefined)

  const flyout = recordIdForFlyout ? (
    <BibliographicRecordFlyout
      recordId={recordIdForFlyout}
      onClose={() => setRecordIdForFlyout(undefined)}
    />
  ) : null

  return (
    <EuiPageTemplate
      grow={false}
      paddingSize='xl'
      restrictWidth={euiTheme.breakpoint.l}
    >
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
              onQuerySubmited={(queryMaterialType, newTerms) => {
                setTerms(newTerms)
                setPage(0)
                setMaterialType(queryMaterialType)
                setQuerySubmittedOnce(true)
              }}
            />
          </EuiPanel>
          {isSearchError && (
            <EuiEmptyPrompt
              body={
                <FormattedMessage
                  defaultMessage='Ocorreu um erro ao realizar a pesquisa'
                  id='search.bibliographic.error'
                />
              }
              color='danger'
              iconType='error'
            />
          )}
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
                    reverse
                    description={
                      <FormattedMessage
                        defaultMessage='Resultados encontrados'
                        id='search.bibliographic.results'
                      />
                    }
                    title={searchResults.search.record_count}
                    titleSize='s'
                  />
                </EuiFlexItem>
                <EuiFlexItem>
                  <EuiFlexGroup alignItems='flexEnd' justifyContent='flexEnd'>
                    <BibliographicSearchResultSort
                      onSortChange={(newSort: EncodedQueryField) => {
                        setSort(newSort)
                        setPage(0)
                      }}
                    />
                  </EuiFlexGroup>
                </EuiFlexItem>
              </EuiFlexGroup>
              <BibliographicSearchResultsTable
                isLoading={isSearchFetching}
                items={searchResults.search.data}
                pagination={getPagination(searchResults)}
                onChange={(criteria) => {
                  setPage(criteria.page?.index ?? 0)
                }}
                onRecordDetailsClick={(record) => {
                  setRecordIdForFlyout(record.id)
                }}
              />
              {flyout}
            </EuiFlexGroup>
          )}
        </EuiFlexGroup>
      </EuiPageTemplate.Section>
    </EuiPageTemplate>
  )
}

function getPagination(
  searchResults: SuccessfulSearchResponse,
): Pagination | undefined {
  if (searchResults.search.page_count > 1) {
    return {
      pageIndex: searchResults.search.page - 1,
      totalItemCount: searchResults.search.record_count,
      pageSize: searchResults.search.records_per_page,
      showPerPageOptions: false,
    }
  }

  return undefined
}

export default BibliographicSearchPage
