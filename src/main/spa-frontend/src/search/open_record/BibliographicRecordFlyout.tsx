import {
  EuiFlexGroup,
  EuiFlyout,
  EuiFlyoutBody,
  EuiFlyoutHeader,
  EuiSkeletonLoading,
  EuiSkeletonRectangle,
  EuiTabbedContent,
  EuiTitle,
  useGeneratedHtmlId,
} from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import { useOpenBibliographicRecord } from '../hooks'

import BibliographicRecordFormPanel from './BibliographicRecordFormPanel'
import BibliographicRecordHoldingsTab from './BibliographicRecordHoldingsTab'
import BibliographicRecordMarcPanel from './BibliographicRecordMarcPanel'
import BibliographicRecordSummaryPanel from './BibliographicRecordSummaryPanel'

import type { FC } from 'react'

type Props = {
  recordId: number
  onClose: () => void
}

const BibliographicRecordFlyout: FC<Props> = ({ recordId, onClose }) => {
  const {
    data: record,
    isSuccess,
    isFetching,
  } = useOpenBibliographicRecord(recordId)

  const flyoutTitleId = useGeneratedHtmlId()

  return (
    <EuiFlyout ownFocus aria-labelledby={flyoutTitleId} onClose={onClose}>
      <EuiFlyoutHeader hasBorder>
        <EuiTitle size='m'>
          <h2 id={flyoutTitleId}>
            <FormattedMessage
              defaultMessage='Detalhes do registro bibliográfico'
              id='bibliographic-record.flyout.title'
            />
          </h2>
        </EuiTitle>
      </EuiFlyoutHeader>
      <EuiFlyoutBody>
        <EuiSkeletonLoading
          isLoading={isFetching}
          loadedContent={
            isSuccess &&
            record.success && (
              <EuiFlexGroup direction='column'>
                <BibliographicRecordSummaryPanel record={record.data} />
                <EuiTabbedContent
                  tabs={[
                    {
                      id: 'summary',
                      content: (
                        <BibliographicRecordSummaryPanel record={record.data} />
                      ),
                      name: (
                        <FormattedMessage
                          defaultMessage='Resumo catalográfico'
                          id='bibliographic-record.flyout.summary'
                        />
                      ),
                    },
                    {
                      id: 'form',
                      content: (
                        <BibliographicRecordFormPanel record={record.data} />
                      ),
                      name: (
                        <FormattedMessage
                          defaultMessage='Formulário'
                          id='bibliographic-record.flyout.marc'
                        />
                      ),
                    },
                    {
                      id: 'marc',
                      content: (
                        <BibliographicRecordMarcPanel marc={record.data.marc} />
                      ),
                      name: (
                        <FormattedMessage
                          defaultMessage='MARC'
                          id='bibliographic-record.flyout.marc'
                        />
                      ),
                    },
                    {
                      id: 'holdings',
                      content: (
                        <BibliographicRecordHoldingsTab record={record.data} />
                      ),
                      name: (
                        <FormattedMessage
                          defaultMessage='Exemplares'
                          id='bibliographic-record.flyout.holdings'
                        />
                      ),
                    },
                  ]}
                />
              </EuiFlexGroup>
            )
          }
          loadingContent={<EuiSkeletonRectangle height={148} width='100%' />}
        />
      </EuiFlyoutBody>
    </EuiFlyout>
  )
}

export default BibliographicRecordFlyout
