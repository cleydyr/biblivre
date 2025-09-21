import { EuiButton } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import { useExportAndDownloadMutation } from './hooks'

import type { FC } from 'react'

import type { BibliographicRecord } from '../api-helpers/search/response-types'

type Props = {
  selectedRecords: BibliographicRecord[]
  onExportSuccess: () => void
}

export const ExportSelectedRecordsButton: FC<Props> = ({
  selectedRecords,
  onExportSuccess,
}) => {
  const { mutate: exportBibliographicRecords, isPending: isExportPending } =
    useExportAndDownloadMutation({
      onSuccess: () => {
        onExportSuccess()
      },
    })

  return (
    <EuiButton
      iconType='exportAction'
      isLoading={isExportPending}
      size='s'
      onClick={() =>
        exportBibliographicRecords(selectedRecords.map((record) => record.id))
      }
    >
      <FormattedMessage
        defaultMessage='Exportar {count, plural, one {# registro} other {# registros}}'
        id='search.export.button'
        values={{ count: selectedRecords.length }}
      />
    </EuiButton>
  )
}

export default ExportSelectedRecordsButton
