import { EuiBadge } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import { useUserType } from '../api-helpers/user-type/hooks'

import type { User } from '../api-helpers/circulation/response-types'

export function UserTypeBadge({ type }: { type: User['type'] }) {
  const { data: userType = null, isLoading } = useUserType(type)

  if (isLoading) {
    return null
  }

  return <EuiBadge color='default'>{userType?.name}</EuiBadge>
}

export function UserStatusBadge({ status }: { status: User['status'] }) {
  switch (status) {
    case 'active':
      return (
        <EuiBadge color='success'>
          <FormattedMessage
            defaultMessage='Ativo'
            id='circulation.users.table.status.active'
          />
        </EuiBadge>
      )
    case 'inactive':
      return (
        <EuiBadge color='danger'>
          <FormattedMessage
            defaultMessage='Inativo'
            id='circulation.users.table.status.inactive'
          />
        </EuiBadge>
      )
    case 'blocked':
      return (
        <EuiBadge color='warning'>
          <FormattedMessage
            defaultMessage='Bloqueado'
            id='circulation.users.table.status.blocked'
          />
        </EuiBadge>
      )
    case 'pending_issues':
      return (
        <EuiBadge color='default'>
          <FormattedMessage
            defaultMessage='Com pendências'
            id='circulation.users.table.status.pending_issues'
          />
        </EuiBadge>
      )
  }
}
