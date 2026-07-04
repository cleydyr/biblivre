import { EuiDescriptionList } from '@elastic/eui'

import { useUserPanelDescriptionListItems } from './lib'

import type { FC } from 'react'

import type { User } from '../api-helpers/circulation/response-types'

type Props = {
  user: User
}

const CirculationUserSummaryPanel: FC<Props> = ({ user }) => {
  return (
    <EuiDescriptionList
      compressed
      listItems={useUserPanelDescriptionListItems(user)}
      type='column'
    />
  )
}

export default CirculationUserSummaryPanel
