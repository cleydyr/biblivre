import {
  EuiButton,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFlyout,
  EuiFlyoutBody,
  EuiFlyoutHeader,
  EuiTitle,
  useEuiTheme,
  useGeneratedHtmlId,
} from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import BibliographicRecordDetails from '../open_record/BibliographicRecordDetails'

import type { FC } from 'react'

type Props = {
  recordId: number
  disableIterateForward: boolean
  disableIterateBackward: boolean
  onClose: () => void
  onIterateForward: () => void
  onIterateBackward?: () => void
}

const BibliographicRecordFlyout: FC<Props> = ({
  recordId,
  disableIterateForward,
  disableIterateBackward,
  onClose,
  onIterateBackward,
  onIterateForward,
}) => {
  const { euiTheme } = useEuiTheme()

  const flyoutTitleId = useGeneratedHtmlId()

  return (
    <EuiFlyout
      ownFocus
      aria-labelledby={flyoutTitleId}
      css={{
        paddingTop: euiTheme.size.xl,
      }}
      onClose={onClose}
    >
      <EuiFlyoutHeader hasBorder>
        <EuiFlexGroup alignItems='center' justifyContent='spaceBetween'>
          <EuiFlexItem>
            <EuiTitle size='m'>
              <h2 id={flyoutTitleId}>
                <FormattedMessage
                  defaultMessage='Detalhes do registro bibliográfico'
                  id='bibliographic-record.flyout.title'
                />
              </h2>
            </EuiTitle>
          </EuiFlexItem>
          <div>
            <EuiFlexGroup>
              <EuiButton
                disabled={disableIterateBackward}
                iconType='chevronSingleLeft'
                onClick={onIterateBackward}
              >
                <FormattedMessage
                  defaultMessage='Anterior'
                  id='bibliographic-record.flyout.back'
                />
              </EuiButton>
              <EuiButton
                disabled={disableIterateForward}
                iconSide='right'
                iconType='chevronSingleRight'
                onClick={onIterateForward}
              >
                <FormattedMessage
                  defaultMessage='Próximo'
                  id='bibliographic-record.flyout.next'
                />
              </EuiButton>
            </EuiFlexGroup>
          </div>
        </EuiFlexGroup>
      </EuiFlyoutHeader>
      <EuiFlyoutBody>
        <BibliographicRecordDetails recordId={recordId} />
      </EuiFlyoutBody>
    </EuiFlyout>
  )
}

export default BibliographicRecordFlyout
