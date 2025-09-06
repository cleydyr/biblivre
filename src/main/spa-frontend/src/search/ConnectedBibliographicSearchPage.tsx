import {
  EuiBasicTable,
  type EuiBasicTableColumn,
  EuiButton,
  type EuiTableSelectionType,
} from '@elastic/eui'
import { Fragment, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import { useSearchCatalographic } from './hooks'

import type { BibliographicRecord } from '../api-helpers/search/response-types'

const ConnectedBibliographicSearchPage = () => {
  const {
    mutate: search,
    isPending,
    isSuccess,
    isError,
    data: searchResults,
  } = useSearchCatalographic()

  const [, setSelectedRecords] = useState<BibliographicRecord[]>([])

  if (isError) {
    return <div>Error</div>
  }

  if (isPending) {
    return <div>Loading...</div>
  }

  const selection: EuiTableSelectionType<BibliographicRecord> = {
    onSelectionChange: (records: BibliographicRecord[]) => {
      setSelectedRecords(records)
    },
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
      <EuiBasicTable
        columns={getBibliographicSearchColumns()}
        itemId='id'
        items={
          isSuccess && searchResults.success ? searchResults.search.data : []
        }
        noItemsMessage={
          <FormattedMessage
            defaultMessage='Nenhum resultado encontrado'
            id='search.bibliographic.no_results'
          />
        }
        selection={selection}
      />
    </Fragment>
  )
}

function getBibliographicSearchColumns(): Array<
  EuiBasicTableColumn<BibliographicRecord>
> {
  return [
    {
      field: 'title',
      name: (
        <FormattedMessage
          defaultMessage='Título'
          id='search.bibliographic.title'
        />
      ),
    },
    {
      field: 'author',
      name: (
        <FormattedMessage
          defaultMessage='Autor'
          id='search.bibliographic.author'
        />
      ),
    },
    {
      field: 'publication_year',
      name: (
        <FormattedMessage
          defaultMessage='Ano de publicação'
          id='search.bibliographic.publication_year'
        />
      ),
    },
    {
      field: 'shelf_location',
      name: (
        <FormattedMessage
          defaultMessage='Localização'
          id='search.bibliographic.shelf_location'
        />
      ),
    },
    {
      field: 'isbn',
      name: (
        <FormattedMessage
          defaultMessage='ISBN'
          id='search.bibliographic.isbn'
        />
      ),
    },
    {
      field: 'subject',
      name: (
        <FormattedMessage
          defaultMessage='Assunto'
          id='search.bibliographic.subject'
        />
      ),
    },
    {
      field: 'holdings_count',
      name: (
        <FormattedMessage
          defaultMessage='Exemplares disponíveis'
          id='search.bibliographic.author'
        />
      ),
    },
  ]
}

export default ConnectedBibliographicSearchPage
