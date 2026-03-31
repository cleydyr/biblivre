import { EuiBasicTable } from '@elastic/eui'
import { Fragment, useState } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'

import ExportSelectedRecordsButton from './ExportSelectedRecordsButton'

import type {
  Criteria,
  EuiBasicTableColumn,
  EuiTableSelectionType,
  Pagination,
} from '@elastic/eui'
import type { FC } from 'react'

import type { BibliographicRecord } from '../api-helpers/search/response-types'

type Props = {
  items: BibliographicRecord[]
  isLoading: boolean
  pagination: Pagination | undefined
  onChange: (criteria: Criteria<BibliographicRecord>) => void
  onRecordDetailsClick: (record: BibliographicRecord) => void
}

export const BibliographicSearchResultsTable: FC<Props> = ({
  items,
  isLoading,
  pagination,
  onChange,
  onRecordDetailsClick,
}) => {
  const [selectedRecords, setSelectedRecords] = useState<BibliographicRecord[]>(
    [],
  )

  const selection: EuiTableSelectionType<BibliographicRecord> = {
    onSelectionChange: setSelectedRecords,
    selected: selectedRecords,
  }

  const columns = useBibliographicSearchColumns(onRecordDetailsClick)

  return (
    <Fragment>
      {selectedRecords.length > 0 && (
        <div>
          <ExportSelectedRecordsButton
            selectedRecords={selectedRecords}
            onExportSuccess={() => setSelectedRecords([])}
          />
        </div>
      )}
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

function useBibliographicSearchColumns(
  onRecordDetailsClick: (record: BibliographicRecord) => void,
): Array<EuiBasicTableColumn<BibliographicRecord>> {
  const { formatMessage } = useIntl()

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
          onClick: onRecordDetailsClick,
          description: formatMessage({
            defaultMessage: 'Ver detalhes do registro',
            id: 'search.bibliographic.actions.details.description',
          }),
        },
      ],
    },
  ]
}

export default BibliographicSearchResultsTable
