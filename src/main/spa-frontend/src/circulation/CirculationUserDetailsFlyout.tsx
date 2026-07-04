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

import CirculationUserDetails from './CirculationUserDetails'

import type { FC } from 'react'

import type { User } from '../api-helpers/circulation/response-types'

type Props = {
  user: User
  disableIterateForward: boolean
  disableIterateBackward: boolean
  onClose: () => void
  onIterateForward: () => void
  onIterateBackward?: () => void
}

const CirculationUserDetailsFlyout: FC<Props> = ({
  user,
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
                  defaultMessage='Detalhes do usuário'
                  id='circulation.user.flyout.title'
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
                  id='circulation.user.flyout.back'
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
                  id='circulation.user.flyout.next'
                />
              </EuiButton>
            </EuiFlexGroup>
          </div>
        </EuiFlexGroup>
      </EuiFlyoutHeader>
      <EuiFlyoutBody>
        <CirculationUserDetails user={user} />
      </EuiFlyoutBody>
    </EuiFlyout>
  )
}

export default CirculationUserDetailsFlyout
