import { EuiButton } from '@elastic/eui'
import { type FC, useEffect } from 'react'
import { FormattedMessage } from 'react-intl'

import {
  useDownloadExportMutation,
  useExportBibliographicRecordsMutation,
} from './hooks'

import type { BibliographicRecord } from '../api-helpers/search/response-types'

type Props = {
  selectedRecords: BibliographicRecord[]
  onExportSuccess: () => void
}

export const ExportSelectedRecordsButton: FC<Props> = ({
  selectedRecords,
  onExportSuccess,
}) => {
  const {
    mutate: exportBibliographicRecords,
    data: exportResponse,
    isSuccess: isExportSuccess,
    isPending: isExportPending,
  } = useExportBibliographicRecordsMutation()

  const { data: downloadExportResponse, mutate: downloadExport } =
    useDownloadExportMutation()

  useEffect(() => {
    if (isExportSuccess && exportResponse.success) {
      downloadExport(exportResponse.uuid)
      onExportSuccess()
    }
  }, [exportResponse, isExportSuccess, onExportSuccess, downloadExport])

  useEffect(() => {
    if (downloadExportResponse) {
      downloadFile(downloadExportResponse)
    }
  }, [downloadExportResponse])

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

function downloadFile(blob: Blob) {
  const link = document.createElement('a')

  const objectURL = URL.createObjectURL(blob)

  link.href = objectURL

  link.click()

  URL.revokeObjectURL(objectURL)
}

export default ExportSelectedRecordsButton
