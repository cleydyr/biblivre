import { EuiBasicTable } from '@elastic/eui'
import { type FC, Fragment, type ReactNode, useState } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'

import BibliographicRecordFlyout from './BibliographicRecordFlyout'

import type {
  Criteria,
  EuiBasicTableColumn,
  EuiTableSelectionType,
  Pagination,
} from '@elastic/eui'

import type { BibliographicRecord } from '../api-helpers/search/response-types'

type Props = {
  items: BibliographicRecord[]
  isLoading: boolean
  pagination: Pagination | undefined
  onChange: (criteria: Criteria<BibliographicRecord>) => void
}

export const BibliographicSearchResultsTable: FC<Props> = ({
  items,
  isLoading,
  pagination,
  onChange,
}) => {
  const [selectedRecords, setSelectedRecords] = useState<BibliographicRecord[]>(
    [],
  )

  const selection: EuiTableSelectionType<BibliographicRecord> = {
    onSelectionChange: setSelectedRecords,
    selected: selectedRecords,
  }

  const { flyout, columns } = useBibliographicSearchColumns()

  return (
    <Fragment>
      {flyout}

      <EuiBasicTable
        columns={columns}
        itemId='id'
        items={items}
        loading={isLoading}
        noItemsMessage={
          <FormattedMessage
            defaultMessage='Nenhum resultado encontrado'
            id='search.bibliographic.no_results'
          />
        }
        pagination={pagination}
        selection={selection}
        tableLayout='auto'
        onChange={onChange}
      />
    </Fragment>
  )
}

type UseBibliographicSearchColumnsReturnType = {
  columns: Array<EuiBasicTableColumn<BibliographicRecord>>
  flyout: ReactNode
}

function useBibliographicSearchColumns(): UseBibliographicSearchColumnsReturnType {
  const { formatMessage } = useIntl()

  const [recordIdForFlyout, setRecordIdForFlyout] = useState<
    number | undefined
  >(undefined)

  const flyout = recordIdForFlyout ? (
    <BibliographicRecordFlyout
      recordId={recordIdForFlyout}
      onClose={() => setRecordIdForFlyout(undefined)}
    />
  ) : null

  return {
    columns: [
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
        align: 'right',
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
            defaultMessage='Ex. disponíveis'
            id='search.bibliographic.holdings_count'
          />
        ),
        align: 'right',
      },
      {
        name: (
          <FormattedMessage
            defaultMessage='Ações'
            id='search.bibliographic.actions'
          />
        ),
        actions: [
          {
            name: (
              <FormattedMessage
                defaultMessage='Ver detalhes'
                id='search.bibliographic.actions.details'
              />
            ),
            icon: 'folderOpen',
            type: 'icon',
            onClick: (record: BibliographicRecord) => {
              setRecordIdForFlyout(record.id)
            },
            description: formatMessage({
              defaultMessage: 'Ver detalhes do registro',
              id: 'search.bibliographic.actions.details.description',
            }),
          },
        ],
      },
    ],
    flyout,
  }
}

export default BibliographicSearchResultsTable
