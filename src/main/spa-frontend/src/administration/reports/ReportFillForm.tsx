import { EuiButton, EuiCallOut, EuiFlexGroup, EuiForm } from '@elastic/eui'

import type { ReportFillRequest, ReportTemplate } from '../../generated-sources'

type ReportFillFormProps = {
  report: ReportTemplate
  onSubmit: (report: ReportFillRequest) => void
  pending: boolean
}

const ReportFillForm = ({ report, onSubmit, pending }: ReportFillFormProps) => {
  return (
    <EuiFlexGroup direction='column'>
      <EuiCallOut
        iconType='iInCircle'
        size='s'
        title='Este modelo de relatório não aceita parâmetros'
      />
      <EuiForm
        onSubmit={(e) => {
          e.preventDefault()
          onSubmit({ reportTemplateId: report.id, parameters: {} })
        }}
      >
        <EuiButton
          fill
          isLoading={pending}
          onClick={() => {
            onSubmit({ reportTemplateId: report.id, parameters: {} })
          }}
        >
          Gerar relatório
        </EuiButton>
      </EuiForm>
    </EuiFlexGroup>
  )
}

export default ReportFillForm
