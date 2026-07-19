import {
  EuiAvatar,
  EuiDescriptionList,
  EuiFlexGroup,
  EuiFlexItem,
  EuiIcon,
  EuiImage,
  EuiModal,
  EuiModalBody,
  EuiModalHeader,
  EuiModalHeaderTitle,
  EuiToolTip,
  useEuiTheme,
  useGeneratedHtmlId,
} from '@elastic/eui'
import { Fragment, useState } from 'react'
import { useIntl } from 'react-intl'

import { getUserPhotoUrl, useUserPanelDescriptionListItems } from './lib'

import type { FC, KeyboardEvent } from 'react'

import type { User } from '../api-helpers/circulation/response-types'

type Props = {
  user: User
}

const CirculationUserAvatar: FC<{ user: User }> = ({ user }) => {
  const { formatMessage } = useIntl()
  const { euiTheme } = useEuiTheme()
  const [isPhotoModalOpen, setIsPhotoModalOpen] = useState(false)
  const photoModalTitleId = useGeneratedHtmlId()
  const photoUrl = user.photo_id ? getUserPhotoUrl(user.photo_id) : undefined
  const photoHint = formatMessage(
    {
      defaultMessage: 'Clique para ver a foto em tamanho real de {name}',
      id: 'circulation.user.photo.view_hint',
    },
    { name: user.name },
  )

  const openPhotoModal = () => {
    if (photoUrl) {
      setIsPhotoModalOpen(true)
    }
  }

  const handleKeyDown = (event: KeyboardEvent<HTMLDivElement>) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault()
      openPhotoModal()
    }
  }

  if (photoUrl) {
    return (
      <Fragment>
        <EuiToolTip content={photoHint} position='top'>
          <div
            aria-label={photoHint}
            css={{
              borderRadius: '50%',
              cursor: 'pointer',
              display: 'inline-block',
              position: 'relative',
              '&:focus-visible': {
                boxShadow: `0 0 0 2px ${euiTheme.colors.primary}`,
                outline: 'none',
              },
              '&:hover': {
                boxShadow: `0 0 0 2px ${euiTheme.colors.primary}`,
              },
            }}
            role='button'
            tabIndex={0}
            onClick={openPhotoModal}
            onKeyDown={handleKeyDown}
          >
            <div
              css={{
                backgroundColor:
                  euiTheme.colors.backgroundBaseInteractiveOverlay,
                borderRadius: '50%',
                flexShrink: 0,
                height: euiTheme.size.xxxxl,
                overflow: 'hidden',
                width: euiTheme.size.xxxxl,
              }}
            >
              <img
                aria-hidden
                alt=''
                css={{
                  display: 'block',
                  height: '100%',
                  objectFit: 'cover',
                  objectPosition: 'center center',
                  width: '100%',
                }}
                src={photoUrl}
              />
            </div>
            <ExpandIcon />
          </div>
        </EuiToolTip>
        {isPhotoModalOpen ? (
          <EuiModal
            aria-labelledby={photoModalTitleId}
            maxWidth={false}
            onClose={() => setIsPhotoModalOpen(false)}
          >
            <EuiModalHeader>
              <EuiModalHeaderTitle id={photoModalTitleId}>
                {user.name}
              </EuiModalHeaderTitle>
            </EuiModalHeader>
            <EuiModalBody>
              <EuiImage
                alt={user.name}
                hasShadow={false}
                size='original'
                src={photoUrl}
              />
            </EuiModalBody>
          </EuiModal>
        ) : null}
      </Fragment>
    )
  }

  return <EuiAvatar iconType='user' name={user.name} size='xl' type='user' />
}

const ExpandIcon: FC = () => {
  const { euiTheme } = useEuiTheme()

  return (
    <span
      aria-hidden
      css={{
        alignItems: 'center',
        backgroundColor: euiTheme.colors.backgroundBaseInteractiveOverlay,
        borderRadius: '50%',
        bottom: 0,
        display: 'flex',
        height: euiTheme.size.l,
        justifyContent: 'center',
        pointerEvents: 'none',
        position: 'absolute',
        right: 0,
        width: euiTheme.size.l,
      }}
    >
      <EuiIcon aria-hidden color='ghost' size='s' type='expand' />
    </span>
  )
}

const CirculationUserSummaryPanel: FC<Props> = ({ user }) => {
  return (
    <EuiFlexGroup alignItems='center' gutterSize='l'>
      <EuiFlexItem grow={false}>
        <CirculationUserAvatar user={user} />
      </EuiFlexItem>
      <EuiFlexItem>
        <EuiDescriptionList
          compressed
          listItems={useUserPanelDescriptionListItems(user)}
          type='column'
        />
      </EuiFlexItem>
    </EuiFlexGroup>
  )
}

export default CirculationUserSummaryPanel
