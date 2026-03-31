import {
  EuiEmptyPrompt,
  EuiFlexGroup,
  EuiSkeletonLoading,
  EuiTabbedContent,
} from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import LoadingState from '../components/LoadingState'
import { useOpenBibliographicRecord } from '../search/hooks'

import RecordAttachments from './RecordAttachments'
import RecordBrief from './RecordBrief'
import RecordForm from './RecordForm'
import RecordHoldings from './RecordHoldings'
import RecordMarc from './RecordMarc'
import RecordSummaryPanel from './RecordSummaryPanel'

import type { FC } from 'react'

type Props = {
  recordId: number
}

const BibliographicRecordDetails: FC<Props> = ({ recordId }) => {
  const {
    data: record,
    isSuccess,
    isError,
    isFetching,
  } = useOpenBibliographicRecord(recordId)

  if (isError) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Ocorreu um erro ao carregar o registro'
            id='bibliographic-record.error'
          />
        }
        color='danger'
        iconType='error'
      />
    )
  }

  if (isSuccess && !record.success) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Registro não encontrado'
            id='bibliographic-record.not-found'
          />
        }
        color='warning'
        iconType='warning'
      />
    )
  }

  return (
    <EuiSkeletonLoading
      isLoading={isFetching}
      loadedContent={
        isSuccess &&
        record.success && (
          <EuiFlexGroup direction='column'>
            <RecordSummaryPanel record={record.data} />
            <EuiTabbedContent
              tabs={[
                {
                  id: 'summary',
                  content: <RecordBrief record={record.data} />,
                  name: (
                    <FormattedMessage
                      defaultMessage='Resumo catalográfico'
                      id='bibliographic-record.flyout.summary'
                    />
                  ),
                },
                {
                  id: 'form',
                  content: <RecordForm record={record.data} />,
                  name: (
                    <FormattedMessage
                      defaultMessage='Formulário'
                      id='bibliographic-record.flyout.form'
                    />
                  ),
                },
                {
                  id: 'marc',
                  content: <RecordMarc marc={record.data.marc} />,
                  name: (
                    <FormattedMessage
                      defaultMessage='MARC'
                      id='bibliographic-record.flyout.marc'
                    />
                  ),
                },
                {
                  id: 'holdings',
                  content: <RecordHoldings record={record.data} />,
                  name: (
                    <FormattedMessage
                      defaultMessage='Exemplares'
                      id='bibliographic-record.flyout.holdings'
                    />
                  ),
                },
                {
                  id: 'attachments',
                  content: <RecordAttachments record={record.data} />,
                  name: (
                    <FormattedMessage
                      defaultMessage='Arquivos digitais'
                      id='bibliographic-record.flyout.attachments'
                    />
                  ),
                },
              ]}
            />
          </EuiFlexGroup>
        )
      }
      loadingContent={<LoadingState />}
    />
  )
}

export default BibliographicRecordDetails
