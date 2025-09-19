import { EuiBadge, EuiFlexGroup, EuiInMemoryTable, EuiStat } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import type { FC } from 'react'

import type { Holding, OpenResult } from '../api-helpers/search/response-types'

type Props = {
  record: OpenResult
}

const BibliographicRecordHoldingsTab: FC<Props> = ({ record }) => {
  return (
    <EuiFlexGroup direction='column'>
      <div />
      <EuiFlexGroup gutterSize='xl'>
        <EuiStat
          description={
            <FormattedMessage
              defaultMessage='Total de exemplares'
              id='bibliographic-record.holdings-tab.holdings_count'
            />
          }
          title={record.holdings_count}
        />
        <EuiStat
          description={
            <FormattedMessage
              defaultMessage='Exemplares disponíveis'
              id='bibliographic-record.holdings-tab.holdings_available'
            />
          }
          title={record.holdings_available}
        />
        <EuiStat
          description={
            <FormattedMessage
              defaultMessage='Exemplares emprestados'
              id='bibliographic-record.holdings-tab.holdings_lent'
            />
          }
          title={record.holdings_lent}
        />
        <EuiStat
          description={
            <FormattedMessage
              defaultMessage='Reservas'
              id='bibliographic-record.holdings-tab.holdings_reserved'
            />
          }
          title={record.holdings_reserved}
        />
      </EuiFlexGroup>
      <EuiInMemoryTable
        columns={[
          {
            name: (
              <FormattedMessage
                defaultMessage='Disponibilidade'
                id='bibliographic-record.holdings-tab.holdings_availability'
              />
            ),
            render: (value: Holding) => {
              return (
                <EuiBadge
                  color={
                    value.availability === 'available' ? 'success' : 'danger'
                  }
                >
                  {value.availability === 'available' ? (
                    <FormattedMessage
                      defaultMessage='Disponível'
                      id='bibliographic-record.holdings-tab.available'
                    />
                  ) : (
                    <FormattedMessage
                      defaultMessage='Indisponível'
                      id='bibliographic-record.holdings-tab.unavailable'
                    />
                  )}
                </EuiBadge>
              )
            },
          },
          {
            field: 'accession_number',
            name: (
              <FormattedMessage
                defaultMessage='Tombo patrimonial'
                id='bibliographic-record.holdings-tab.accession_number'
              />
            ),
          },

          {
            field: 'shelf_location',
            name: (
              <FormattedMessage
                defaultMessage='Localização'
                id='bibliographic-record.holdings-tab.shelf_location'
              />
            ),
          },
          {
            field: 'id',
            name: (
              <FormattedMessage
                defaultMessage='Nº do exemplar'
                id='bibliographic-record.holdings-tab.id'
              />
            ),
          },
        ]}
        items={record.holdings ?? []}
      />
    </EuiFlexGroup>
  )
}

export default BibliographicRecordHoldingsTab
