import { EuiBasicTable } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import type { BibliographicRecord } from '../api-helpers/search/response-types'
import type { EuiBasicTableColumn, EuiTableSelectionType } from '@elastic/eui'
import type { FC } from 'react'

type Props = {
  items: BibliographicRecord[]
  isPending: boolean
  onSelectItems: (items: BibliographicRecord[]) => void
}

export const BibliographicSearchResultsTable: FC<Props> = ({
  items,
  isPending,
  onSelectItems,
}) => {
  const selection: EuiTableSelectionType<BibliographicRecord> = {
    onSelectionChange: onSelectItems,
  }

  return (
    <EuiBasicTable
      columns={getBibliographicSearchColumns()}
      itemId='id'
      items={items}
      loading={isPending}
      noItemsMessage={
        <FormattedMessage
          defaultMessage='Nenhum resultado encontrado'
          id='search.bibliographic.no_results'
        />
      }
      selection={selection}
    />
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
          id='search.bibliographic.holdings_count'
        />
      ),
    },
  ]
}

export default BibliographicSearchResultsTable
