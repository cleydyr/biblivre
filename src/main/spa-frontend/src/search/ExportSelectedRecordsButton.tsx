import { EuiButton } from '@elastic/eui'
import { type FC, useEffect } from 'react'
import { FormattedMessage } from 'react-intl'

import { useExportAndDownloadMutation } from './hooks'

import type { BibliographicRecord } from '../api-helpers/search/response-types'
import type { FileDownload } from '../api-helpers/types'

type Props = {
  selectedRecords: BibliographicRecord[]
  onExportSuccess: () => void
}

export const ExportSelectedRecordsButton: FC<Props> = ({
  selectedRecords,
  onExportSuccess,
}) => {
  const {
    data: downloadExportResponse,
    mutate: exportBibliographicRecords,
    isPending: isExportPending,
  } = useExportAndDownloadMutation()

  useEffect(() => {
    if (downloadExportResponse) {
      downloadFile(downloadExportResponse)
      onExportSuccess()
    }
  }, [downloadExportResponse, onExportSuccess])

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

function downloadFile({ blob, filename }: FileDownload) {
  const link = document.createElement('a')

  const objectURL = URL.createObjectURL(blob)

  link.href = objectURL

  link.download = filename ?? 'biblivre_download'

  link.click()

  URL.revokeObjectURL(objectURL)
}

export default ExportSelectedRecordsButton
