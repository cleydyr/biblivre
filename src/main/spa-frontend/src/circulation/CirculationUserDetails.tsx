import { EuiFlexGroup } from '@elastic/eui'
import { lazy, Suspense } from 'react'
import { FormattedMessage } from 'react-intl'

import LoadingState from '../components/LoadingState'
import SpacedEuiTabbedContent from '../components/SpacedEuiTabbedContent'

import CirculationUserSummaryPanel from './CirculationUserSummaryPanel'

import type { EuiTabbedContentTab } from '@elastic/eui'
import type { FC, ReactNode } from 'react'

import type { User } from '../api-helpers/circulation/response-types'
import type { CirculationUserTab } from '../api-helpers/circulation/types'

type Props = {
  user: User
}

type TypedEuiTabbedContentTab<T> = Omit<EuiTabbedContentTab, 'id'> & {
  id: T
}

const CirculationUserLendingsTab = lazy(
  () => import('./CirculationUserLendingsTab'),
)
const CirculationUserReservationsTab = lazy(
  () => import('./CirculationUserReservationsTab'),
)
const CirculationUserFinesTab = lazy(() => import('./CirculationUserFinesTab'))

const LazyTabContent: FC<{ children: ReactNode }> = ({ children }) => (
  <Suspense fallback={<LoadingState />}>{children}</Suspense>
)

const CirculationUserDetails: FC<Props> = ({ user }) => {
  const tabs: TypedEuiTabbedContentTab<CirculationUserTab>[] = [
    {
      id: 'lendings',
      content: (
        <LazyTabContent>
          <CirculationUserLendingsTab userId={user.id} />
        </LazyTabContent>
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
        <LazyTabContent>
          <CirculationUserReservationsTab userId={user.id} />
        </LazyTabContent>
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
        <LazyTabContent>
          <CirculationUserFinesTab userId={user.id} />
        </LazyTabContent>
      ),
      name: (
        <FormattedMessage
          defaultMessage='Multas'
          id='circulation.user.tabs.fines'
        />
      ),
    },
  ]

  return (
    <EuiFlexGroup direction='column'>
      <CirculationUserSummaryPanel user={user} />
      <SpacedEuiTabbedContent
        initiallySelectedTabId='lendings'
        spacing='m'
        tabs={tabs}
      />
    </EuiFlexGroup>
  )
}

export default CirculationUserDetails
