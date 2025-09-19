import {
  EuiFlyout,
  EuiFlyoutBody,
  EuiFlyoutHeader,
  EuiTitle,
  useGeneratedHtmlId,
} from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import BibliographicRecordDetails from '../open_record/BibliographicRecordDetails'

import type { FC } from 'react'

type Props = {
  recordId: number
  onClose: () => void
}

const BibliographicRecordFlyout: FC<Props> = ({ recordId, onClose }) => {
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
        <BibliographicRecordDetails recordId={recordId} />
      </EuiFlyoutBody>
    </EuiFlyout>
  )
}

export default BibliographicRecordFlyout
