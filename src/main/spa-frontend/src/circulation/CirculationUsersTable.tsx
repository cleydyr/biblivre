import {
  EuiBadge,
  EuiBasicTable,
  EuiEmptyPrompt,
  EuiProgress,
  EuiSkeletonText,
  useEuiTheme,
} from '@elastic/eui'
import { FormattedMessage, useIntl } from 'react-intl'

import { useUserType } from '../api-helpers/user-type/hooks'

import type {
  Criteria,
  EuiBadgeProps,
  EuiBasicTableColumn,
  Pagination,
} from '@elastic/eui'
import type { FC } from 'react'

import type { User } from '../api-helpers/circulation/response-types'

type CirculationUsersTableProps = {
  users: User[]
  isLoading: boolean
  onUserDetailsClick: (user: User) => void
  onEditUser: (user: User) => void
  onBlockUser: (user: User) => void
  onUnblockUser: (user: User) => void
  onDeactivateOrDeleteUser: (user: User) => void
  statusChangeUserId?: number | null
  pagination: Pagination | undefined
  onPaginate: (pageIndex: number) => void
}

const CirculationUsersTable: FC<CirculationUsersTableProps> = ({
  users,
  isLoading,
  onUserDetailsClick,
  onEditUser,
  onBlockUser,
  onUnblockUser,
  onDeactivateOrDeleteUser,
  statusChangeUserId = null,
  pagination,
  onPaginate,
}) => {
  const { formatMessage } = useIntl()

  const columns: Array<EuiBasicTableColumn<User>> = [
    {
      field: 'id',
      name: 'Matrícula',
      sortable: true,
    },
    {
      field: 'name',
      name: 'Nome',
      sortable: true,
    },
    {
      field: 'type',
      name: 'Tipo',
      render: (type: User['type']) => {
        return <UserType type={type} />
      },
    },
    {
      name: 'Situação',
      render: (user: User) => {
        return (
          <UserStatusCell
            isChanging={statusChangeUserId === user.id}
            status={user.status}
          />
        )
      },
    },
    {
      field: 'created',
      name: 'Criado em',
      render: (createdAt: User['created']) => {
        return Intl.DateTimeFormat('pt-BR', {
          dateStyle: 'short',
          timeStyle: 'short',
        }).format(new Date(createdAt))
      },
    },
    // actions column
    {
      name: (
        <FormattedMessage
          defaultMessage='Ações'
          id='search.bibliographic.actions'
        />
      ),
      actions: [
        {
          name: <FormattedMessage defaultMessage='Editar' id='common.edit' />,
          icon: 'pencil',
          type: 'icon',
          onClick: onEditUser,
          description: formatMessage({
            defaultMessage: 'Editar usuário',
            id: 'circulation.users.table.actions.edit.description',
          }),
        },
        {
          name: (
            <FormattedMessage
              defaultMessage='Ver detalhes'
              id='search.bibliographic.actions.details'
            />
          ),
          icon: 'folderOpen',
          type: 'icon',
          onClick: onUserDetailsClick,
          description: formatMessage({
            defaultMessage: 'Ver detalhes do usuário',
            id: 'circulation.users.table.actions.details.description',
          }),
          isPrimary: true,
        },
        {
          name: (
            <FormattedMessage
              defaultMessage='Bloquear'
              id='circulation.user.button.block'
            />
          ),
          icon: 'lock',
          type: 'icon',
          color: 'danger',
          available: (user) => user.status !== 'blocked',
          enabled: (user) => statusChangeUserId !== user.id,
          onClick: onBlockUser,
          description: formatMessage({
            defaultMessage: 'Bloquear usuário',
            id: 'circulation.users.table.actions.block.description',
          }),
          isPrimary: true,
        },
        {
          name: (
            <FormattedMessage
              defaultMessage='Desbloquear'
              id='circulation.user.button.unblock'
            />
          ),
          icon: 'lockOpen',
          type: 'icon',
          color: 'success',
          available: (user) => user.status === 'blocked',
          enabled: (user) => statusChangeUserId !== user.id,
          onClick: onUnblockUser,
          description: formatMessage({
            defaultMessage: 'Desbloquear usuário',
            id: 'circulation.users.table.actions.unblock.description',
          }),
        },
        {
          name: (user: User) =>
            user.status === 'inactive' ? (
              <FormattedMessage defaultMessage='Excluir' id='common.delete' />
            ) : (
              <FormattedMessage
                defaultMessage='Desativar'
                id='circulation.users.table.actions.deactivate'
              />
            ),
          icon: (user: User) =>
            user.status === 'inactive' ? 'trash' : 'minusInCircle',
          type: 'icon',
          color: 'danger',
          enabled: (user) => statusChangeUserId !== user.id,
          onClick: onDeactivateOrDeleteUser,
          description: (user: User) =>
            user.status === 'inactive'
              ? formatMessage({
                  defaultMessage: 'Excluir usuário permanentemente',
                  id: 'circulation.users.table.actions.delete.description',
                })
              : formatMessage({
                  defaultMessage: 'Marcar usuário como inativo',
                  id: 'circulation.users.table.actions.deactivate.description',
                }),
        },
      ],
    },
  ]

  return (
    <EuiBasicTable<User>
      columns={columns}
      items={users}
      loading={isLoading}
      noItemsMessage={
        <EuiEmptyPrompt
          body={
            <FormattedMessage
              defaultMessage='Não foi possível encontrar nenhum usuário com os termos de busca e filtros aplicados'
              id='circulation.users.table.empty.body'
            />
          }
          title={
            <FormattedMessage
              defaultMessage='Nenhum usuário encontrado'
              id='circulation.users.table.empty.title'
            />
          }
        />
      }
      pagination={
        pagination && {
          ...pagination,
          showPerPageOptions: false,
        }
      }
      rowProps={(user) =>
        statusChangeUserId === user.id
          ? {
              'aria-busy': true,
            }
          : {}
      }
      tableCaption={formatMessage({
        defaultMessage: 'Resultados da busca de usuários',
        id: 'circulation.users.table.caption',
      })}
      tableLayout='auto'
      onChange={(criteria: Criteria<User>) => {
        onPaginate(criteria.page?.index ?? 0)
      }}
    />
  )
}

const UserStatusCell: FC<{
  status: User['status']
  isChanging: boolean
}> = ({ status, isChanging }) => {
  const { euiTheme } = useEuiTheme()

  return (
    <div
      aria-busy={isChanging}
      css={{
        position: 'relative',
        width: '100%',
        paddingBottom: euiTheme.size.xxs,
      }}
    >
      <EuiBadge color={getStatusColor(status)} iconType={getStatusIcon(status)}>
        {getStatusLabel(status)}
      </EuiBadge>
      {isChanging && (
        <EuiProgress
          color='primary'
          css={{
            top: 'auto',
            bottom: 0,
          }}
          position='absolute'
          size='xs'
        />
      )}
    </div>
  )
}

const getStatusColor = (status: User['status']): EuiBadgeProps['color'] => {
  switch (status) {
    case 'active':
      return 'success'
    case 'inactive':
      return 'danger'
    case 'blocked':
      return 'warning'
    case 'pending_issues':
      return 'default'
  }
}

const getStatusLabel = (status: User['status']) => {
  switch (status) {
    case 'active':
      return (
        <FormattedMessage
          defaultMessage='Ativo'
          id='circulation.users.table.status.active'
        />
      )
    case 'inactive':
      return (
        <FormattedMessage
          defaultMessage='Inativo'
          id='circulation.users.table.status.inactive'
        />
      )
    case 'blocked':
      return (
        <FormattedMessage
          defaultMessage='Bloqueado'
          id='circulation.users.table.status.blocked'
        />
      )
    case 'pending_issues':
      return (
        <FormattedMessage
          defaultMessage='Com pendências'
          id='circulation.users.table.status.pending_issues'
        />
      )
    default:
      return status
  }
}

const getStatusIcon = (status: User['status']) => {
  switch (status) {
    case 'active':
      return 'check'
    case 'inactive':
      return 'crossInCircle'
    case 'blocked':
      return 'lock'
    case 'pending_issues':
      return 'alert'
    default:
      return 'default'
  }
}

const UserType: FC<{ type: User['type'] }> = ({ type }) => {
  const { data: userType = null, isLoading } = useUserType(type)

  if (isLoading) {
    return <EuiSkeletonText lines={1} />
  }

  return <EuiBadge color='default'>{userType?.name}</EuiBadge>
}

export default CirculationUsersTable
