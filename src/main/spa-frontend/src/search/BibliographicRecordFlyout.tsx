import {
  EuiCodeBlock,
  EuiFlyout,
  EuiFlyoutBody,
  EuiFlyoutHeader,
  EuiTitle,
  useGeneratedHtmlId,
} from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import { useOpenBibliographicRecord } from './hooks'

import type { FC } from 'react'

type Props = {
  recordId: number
  onClose: () => void
}

const BibliographicRecordFlyout: FC<Props> = ({ recordId, onClose }) => {
  const { data, isSuccess } = useOpenBibliographicRecord(recordId)

  const flyoutTitleId = useGeneratedHtmlId()

  return (
    isSuccess &&
    data.success && (
      <EuiFlyout ownFocus aria-labelledby={flyoutTitleId} onClose={onClose}>
        <EuiFlyoutHeader hasBorder>
          <EuiTitle size='m'>
            <h2 id={flyoutTitleId}>
              <FormattedMessage
                defaultMessage='Registro {id}'
                id='bibliographic-record.flyout.title'
                values={{ id: data.success && data.data.id }}
              />
            </h2>
          </EuiTitle>
        </EuiFlyoutHeader>
        <EuiFlyoutBody>
          <EuiCodeBlock language='html'>{data.data.marc}</EuiCodeBlock>
        </EuiFlyoutBody>
      </EuiFlyout>
    )
  )
}

export default BibliographicRecordFlyout
