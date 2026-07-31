import {
  EuiButton,
  EuiCallOut,
  EuiEmptyPrompt,
  EuiFlexGroup,
  EuiFlexItem,
  EuiStat,
} from '@elastic/eui'
import { useEffect, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import { downloadFile } from '../api-helpers/lib'
import {
  downloadSearchExcel,
  prepareSearchExcelExport,
} from '../api-helpers/search'
import { FIELDS } from '../api-helpers/search/constants'
import { getSearchMode, getSearchTerms } from '../api-helpers/search/lib'
import FeatureFlag from '../components/FeatureFlag'
import { SEARCH_EXCEL_EXPORT_FEATURE } from '../config/features'
import BibliographicSearchResultSort from './BibliographicSearchResultSort'
import BibliographicSearchResultsTable from './BibliographicSearchResultsTable'
import { getPagination, isFirstResult, isLastResult } from './lib'
import BibliographicRecordFlyout from './RecordFlyout'

import type { FC } from 'react'

import type { SearchResponse } from '../api-helpers/search/response-types'
import type {
  BibliographicMaterial,
  EncodedQueryField,
  SearchMode,
  SearchQueryTerms,
} from '../api-helpers/search/types'

type Props = {
  searchResults: SearchResponse | undefined
  isSearchSuccess: boolean
  isSearchError: boolean
  isSearchFetching: boolean
  materialType: BibliographicMaterial
  terms: SearchQueryTerms | undefined
  searchMode: SearchMode
  sort: EncodedQueryField | undefined
  onSortChange: (sort: EncodedQueryField) => void
  onPageChange: (page: number) => void
}

const BibliographicSearchResults: FC<Props> = ({
  searchResults,
  isSearchSuccess,
  isSearchError,
  isSearchFetching,
  materialType,
  terms,
  searchMode,
  sort,
  onSortChange,
  onPageChange,
}) => {
  const [isExportingExcel, setIsExportingExcel] = useState(false)
  const [excelExportError, setExcelExportError] = useState(false)
  const [showFlyout, setShowFlyout] = useState(false)
  const [recordIdForFlyout, setRecordIdForFlyout] = useState<
    number | undefined
  >(undefined)

  useEffect(() => {
    if (
      showFlyout &&
      isSearchSuccess &&
      searchResults?.success &&
      searchResults.search.data.length > 0 &&
      recordIdForFlyout === undefined
    ) {
      setRecordIdForFlyout(searchResults.search.data[0].id)
    }
  }, [showFlyout, isSearchSuccess, searchResults, recordIdForFlyout])

  if (isSearchError) {
    return (
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
    )
  }

  if (isSearchSuccess && searchResults?.success === false) {
    return (
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
    )
  }

  if (!(isSearchSuccess && searchResults?.success)) {
    return null
  }

  const flyout = showFlyout && (
    <BibliographicRecordFlyout
      disableIterateBackward={
        isSearchFetching || isFirstResult(searchResults, recordIdForFlyout)
      }
      disableIterateForward={
        isSearchFetching || isLastResult(searchResults, recordIdForFlyout)
      }
      recordId={recordIdForFlyout ?? 0}
      onClose={() => {
        setShowFlyout(false)
        setRecordIdForFlyout(undefined)
      }}
      onIterateBackward={() => {
        const indexOfRecord = searchResults.search.data.findIndex(
          (record) => record.id === recordIdForFlyout,
        )

        if (indexOfRecord > 0) {
          setRecordIdForFlyout(searchResults.search.data[indexOfRecord - 1].id)

          return
        }

        if (searchResults.search.page > 1) {
          onPageChange(searchResults.search.page - 1)
          setRecordIdForFlyout(undefined)
        }
      }}
      onIterateForward={() => {
        const indexOfRecord = searchResults.search.data.findIndex(
          (record) => record.id === recordIdForFlyout,
        )

        if (indexOfRecord < searchResults.search.data.length - 1) {
          setRecordIdForFlyout(searchResults.search.data[indexOfRecord + 1].id)

          return
        }

        if (searchResults.search.page < searchResults.search.page_count) {
          onPageChange(searchResults.search.page)
          setRecordIdForFlyout(undefined)
        }
      }}
    />
  )

  return (
    <EuiFlexGroup direction='column'>
      <FeatureFlag name={SEARCH_EXCEL_EXPORT_FEATURE}>
        {excelExportError && (
          <EuiCallOut
            announceOnMount
            color='danger'
            iconType='error'
            title={
              <FormattedMessage
                defaultMessage='Não foi possível exportar os resultados.'
                id='search.bibliographic.export_excel_error'
              />
            }
          />
        )}
      </FeatureFlag>
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
        <FeatureFlag name={SEARCH_EXCEL_EXPORT_FEATURE}>
          <EuiFlexItem grow={false}>
            <EuiButton
              color='primary'
              iconType='exportAction'
              isDisabled={searchResults.search.data.length === 0}
              isLoading={isExportingExcel}
              onClick={async () => {
                setExcelExportError(false)
                setIsExportingExcel(true)
                try {
                  const search_parameters = JSON.stringify({
                    database: 'main',
                    material_type: materialType,
                    search_mode: getSearchMode(terms, searchMode),
                    ...getSearchTerms(terms),
                  })
                  const sortParam =
                    sort === undefined ? FIELDS.TITLE : String(sort)
                  const res = await prepareSearchExcelExport(
                    search_parameters,
                    sortParam,
                  )
                  if (!res.success) {
                    setExcelExportError(true)

                    return
                  }
                  const file = await downloadSearchExcel(res.uuid)
                  downloadFile(file)
                } catch {
                  setExcelExportError(true)
                } finally {
                  setIsExportingExcel(false)
                }
              }}
            >
              <FormattedMessage
                defaultMessage='Exportar resultados (Excel)'
                id='search.bibliographic.export_excel'
              />
            </EuiButton>
          </EuiFlexItem>
        </FeatureFlag>
        <EuiFlexItem>
          <EuiFlexGroup alignItems='flexEnd' justifyContent='flexEnd'>
            <BibliographicSearchResultSort
              onSortChange={(newSort: EncodedQueryField) => {
                onSortChange(newSort)
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
          onPageChange(criteria.page?.index ?? 0)
        }}
        onRecordDetailsClick={(record) => {
          setRecordIdForFlyout(record.id)
          setShowFlyout(true)
        }}
      />
      {flyout}
    </EuiFlexGroup>
  )
}

export default BibliographicSearchResults
