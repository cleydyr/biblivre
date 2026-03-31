import {
  EuiButton,
  EuiForm,
  EuiFormRow,
  EuiSelect,
  EuiSpacer,
} from '@elastic/eui'
import { useState } from 'react'

import type { Report } from './api'
import type { ReportStatus } from './types'

type GenerateReportFormProps = {
  reports: Report[] | undefined
  isLoading: boolean
  reportStatus: ReportStatus
  pdfUrl: string | undefined
  onSubmit: (report: Report) => void
}

export const GenerateReportForm = ({
  reports = [],
  isLoading,
  reportStatus,
  pdfUrl,
  onSubmit,
}: GenerateReportFormProps) => {
  const [selectedReport, setSelectedReport] = useState(
    undefined as Report | undefined,
  )

  function getGenerateReportButtonLabel(): string {
    return reportStatus === 'GENERATING'
      ? 'Gerando relatório'
      : reportStatus === 'PENDING'
        ? 'Gerar relatório'
        : 'Baixar relatório'
  }

  function getCurrentDateAndHour() {
    return new Date()
      .toLocaleString()
      .replace(/\//g, '-')
      .replace(/:/g, '-')
      .replace(/,/g, '')
      .replace(/ /g, '_')
  }

  function downloadPDFFile(): void {
    if (!pdfUrl) {
      throw new Error('No PDF file available')
    }

    if (!selectedReport) {
      throw new Error('No report selected')
    }

    const a = document.createElement('a')
    a.href = pdfUrl
    a.download = `${selectedReport.name}_${getCurrentDateAndHour()}.pdf`
    a.click()
  }

  return (
    <EuiForm>
      <EuiFormRow label='Selecione um relatório'>
        <EuiSelect
          hasNoInitialSelection
          disabled={isLoading}
          isLoading={isLoading}
          options={reports.map((report: Report) => ({
            value: report.id,
            text: report.name,
          }))}
          value={selectedReport?.id?.toString()}
          onChange={(e) => {
            setSelectedReport(
              reports.find(
                (report) => report.id?.toString() === e.target.value,
              ),
            )
          }}
        />
      </EuiFormRow>
      <EuiSpacer />
      <EuiFormRow>
        <EuiButton
          fill
          aria-disabled={!selectedReport || reportStatus === 'GENERATING'}
          aria-label={getGenerateReportButtonLabel()}
          isLoading={reportStatus === 'GENERATING'}
          onClick={
            reportStatus === 'PENDING'
              ? () => onSubmit(selectedReport as Report)
              : reportStatus === 'READY'
                ? downloadPDFFile
                : () => {}
          }
        >
          {getGenerateReportButtonLabel()}
        </EuiButton>
      </EuiFormRow>
    </EuiForm>
  )
}
