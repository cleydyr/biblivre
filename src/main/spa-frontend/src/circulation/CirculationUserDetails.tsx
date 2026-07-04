import { EuiFlexGroup, EuiTabbedContent } from '@elastic/eui'
import { useMemo, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import CirculationUserFinesTab from './CirculationUserFinesTab'
import CirculationUserLendingsTab from './CirculationUserLendingsTab'
import CirculationUserReservationsTab from './CirculationUserReservationsTab'
import CirculationUserSummaryPanel from './CirculationUserSummaryPanel'

import type { EuiTabbedContentTab } from '@elastic/eui'
import type { FC } from 'react'

import type { User } from '../api-helpers/circulation/response-types'
import type { CirculationUserTab } from '../api-helpers/circulation/types'

type Props = {
  user: User
}

type TypedEuiTabbedContentTab<T> = Omit<EuiTabbedContentTab, 'id'> & {
  id: T
}

const CirculationUserDetails: FC<Props> = ({ user }) => {
  const [selectedTabId, setSelectedTabId] =
    useState<CirculationUserTab>('lendings')

  const tabs = useMemo<TypedEuiTabbedContentTab<CirculationUserTab>[]>(
    () => [
      {
        id: 'lendings',
        content: (
          <CirculationUserLendingsTab
            enabled={selectedTabId === 'lendings'}
            userId={user.id}
          />
        ),
        name: (
          <FormattedMessage
            defaultMessage='Empréstimos'
            id='circulation.user.tabs.lendings'
          />
        ),
      },
      {
        id: 'reservations',
        content: (
          <CirculationUserReservationsTab
            enabled={selectedTabId === 'reservations'}
            userId={user.id}
          />
        ),
        name: (
          <FormattedMessage
            defaultMessage='Reservas'
            id='circulation.user.tabs.reservations'
          />
        ),
      },
      {
        id: 'fines',
        content: (
          <CirculationUserFinesTab
            enabled={selectedTabId === 'fines'}
            userId={user.id}
          />
        ),
        name: (
          <FormattedMessage
            defaultMessage='Multas'
            id='circulation.user.tabs.fines'
          />
        ),
      },
    ],
    [selectedTabId, user.id],
  )

  const selectedTab = tabs.find((tab) => tab.id === selectedTabId) ?? tabs[0]

  return (
    <EuiFlexGroup direction='column'>
      <CirculationUserSummaryPanel user={user} />
      <EuiTabbedContent
        initialSelectedTab={tabs[0]}
        selectedTab={selectedTab}
        tabs={tabs}
        onTabClick={(tab) => setSelectedTabId(tab.id as CirculationUserTab)}
      />
    </EuiFlexGroup>
  )
}

export default CirculationUserDetails
