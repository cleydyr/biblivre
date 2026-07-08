import {
  EuiButton,
  EuiConfirmModal,
  EuiEmptyPrompt,
  EuiGlobalToastList,
  useGeneratedHtmlId,
} from '@elastic/eui'
import { useCallback, useMemo, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import {
  useBlockCirculationUserMutation,
  useCirculationUsersPaginateMutation,
  useCirculationUsersSearchMutation,
  useDeleteCirculationUserMutation,
  useUnblockCirculationUserMutation,
} from '../api-helpers/circulation/hooks'
import useLatch from '../hooks/useLatch'

import CirculationUserDetailsFlyout from './CirculationUserDetailsFlyout'
import CirculationUsersControls from './CirculationUsersControls'
import CirculationUsersTable from './CirculationUsersTable'
import { toCirculationSearchPayload } from './lib'

import type { Pagination } from '@elastic/eui'
import type { Toast } from '@elastic/eui/src/components/toast/global_toast_list'

import type {
  CirculationUsersSearchResponse,
  User,
} from '../api-helpers/circulation/response-types'

import type { CirculationSearchControlConfig } from './types'

const CirculationUsersControlsAndTable = () => {
  const {
    mutate: searchUsers,
    isPending: isSearching,
    data: searchResults,
    isSuccess: isSearchSuccess,
  } = useCirculationUsersSearchMutation()

  const {
    mutate: paginateUsers,
    isPending: isPaginating,
    data: paginateResults,
    isSuccess: isPaginateSuccess,
  } = useCirculationUsersPaginateMutation()

  const {
    mutate: blockUser,
    isPending: isBlockingUser,
    variables: blockingUserId,
  } = useBlockCirculationUserMutation()

  const {
    mutate: unblockUser,
    isPending: isUnblockingUser,
    variables: unblockingUserId,
  } = useUnblockCirculationUserMutation()

  const {
    mutate: deleteUser,
    isPending: isDeletingUser,
    variables: deletingUserId,
  } = useDeleteCirculationUserMutation()

  const [usePaginatedResults, setUsePaginatedResults] = useState<boolean>(false)

  const [selectedUser, setSelectedUser] = useState<User | null>(null)
  const [userPendingDelete, setUserPendingDelete] = useState<User | null>(null)
  const [userStatusOverrides, setUserStatusOverrides] = useState<
    Record<number, User['status']>
  >({})
  const [removedUserIds, setRemovedUserIds] = useState<number[]>([])
  const [toasts, setToasts] = useState<Toast[]>([])

  const { value: submitted, latch: latchSubmitted } = useLatch()

  const users: User[] | undefined = getUsers(
    usePaginatedResults,
    paginateResults,
    searchResults,
  )

  const usersWithStatusOverrides = useMemo(() => {
    if (users === undefined) {
      return undefined
    }

    return users
      .filter((user) => !removedUserIds.includes(user.id))
      .map((user) => {
        if (user.id in userStatusOverrides) {
          return { ...user, status: userStatusOverrides[user.id] }
        }

        return user
      })
  }, [users, userStatusOverrides, removedUserIds])

  const statusChangeUserId = isBlockingUser
    ? blockingUserId
    : isUnblockingUser
      ? unblockingUserId
      : isDeletingUser
        ? deletingUserId
        : null

  const removeToast = useCallback((toast: Toast) => {
    setToasts((currentToasts) =>
      currentToasts.filter((currentToast) => currentToast.id !== toast.id),
    )
  }, [])

  const showStatusChangeToast = useCallback(
    (response: { success: boolean; message?: string }, userId: number) => {
      setToasts([
        {
          id: `user-status-change-${userId}-${Date.now()}`,
          title: response.message ?? '',
          color: response.success ? 'success' : 'warning',
          iconType: response.success ? 'check' : 'alert',
        },
      ])
    },
    [],
  )

  const applyUserStatusChange = useCallback(
    (userId: number, status: User['status']) => {
      setUserStatusOverrides((currentOverrides) => ({
        ...currentOverrides,
        [userId]: status,
      }))
      setSelectedUser((currentUser) =>
        currentUser?.id === userId ? { ...currentUser, status } : currentUser,
      )
    },
    [],
  )

  const onBlockUser = useCallback(
    (user: User) => {
      blockUser(user.id, {
        onSuccess: (response) => {
          showStatusChangeToast(response, user.id)

          if (response.success) {
            applyUserStatusChange(user.id, 'blocked')
          }
        },
      })
    },
    [applyUserStatusChange, blockUser, showStatusChangeToast],
  )

  const onUnblockUser = useCallback(
    (user: User) => {
      unblockUser(user.id, {
        onSuccess: (response) => {
          showStatusChangeToast(response, user.id)

          if (response.success) {
            applyUserStatusChange(user.id, 'active')
          }
        },
      })
    },
    [applyUserStatusChange, showStatusChangeToast, unblockUser],
  )

  const onDeactivateOrDeleteUser = useCallback((user: User) => {
    setUserPendingDelete(user)
  }, [])

  const onConfirmDeactivateOrDeleteUser = useCallback(() => {
    if (userPendingDelete === null) {
      return
    }

    const user = userPendingDelete
    const isPermanentDelete = user.status === 'inactive'

    deleteUser(user.id, {
      onSuccess: (response) => {
        showStatusChangeToast(response, user.id)

        if (response.success) {
          if (isPermanentDelete) {
            setRemovedUserIds((currentRemovedUserIds) => [
              ...currentRemovedUserIds,
              user.id,
            ])
            setSelectedUser((currentUser) =>
              currentUser?.id === user.id ? null : currentUser,
            )
          } else {
            applyUserStatusChange(user.id, 'inactive')
          }
        }

        setUserPendingDelete(null)
      },
      onError: () => {
        setUserPendingDelete(null)
      },
    })
  }, [
    applyUserStatusChange,
    deleteUser,
    showStatusChangeToast,
    userPendingDelete,
  ])

  const selectedUserIndex =
    selectedUser && usersWithStatusOverrides
      ? usersWithStatusOverrides.findIndex(
          (user) => user.id === selectedUser.id,
        )
      : -1

  const pagination: Pagination | undefined = useMemo(() => {
    if (searchResults === undefined || searchResults.search.page_count <= 1) {
      return undefined
    }

    if (usePaginatedResults) {
      if (
        paginateResults === undefined ||
        paginateResults.search.page_count <= 1
      ) {
        return undefined
      }

      return {
        pageIndex: paginateResults.search.page - 1,
        pageSize: paginateResults.search.records_per_page,
        totalItemCount: paginateResults.search.record_count,
      }
    }

    return {
      pageIndex: searchResults.search.page - 1,
      pageSize: searchResults.search.records_per_page,
      totalItemCount: searchResults.search.record_count,
    }
  }, [searchResults, paginateResults, usePaginatedResults])

  const onSearchUsers = () => {
    setUsePaginatedResults(false)
    setUserStatusOverrides({})
    setRemovedUserIds([])
    searchUsers(toCirculationSearchPayload(searchConfig))
    latchSubmitted()
  }

  const onPaginateUsers = (pageIndex: number) => {
    setUsePaginatedResults(true)
    paginateUsers({
      ...toCirculationSearchPayload(searchConfig),
      page: pageIndex + 1,
    })
  }

  const [searchConfig, setSearchConfig] =
    useState<CirculationSearchControlConfig>({
      query: '',
      searchField: '',
      isAdvancedSearch: false,
    })

  return (
    <div>
      <CirculationUsersControls
        isLoading={false}
        searchConfig={searchConfig}
        onSearchConfigChange={setSearchConfig}
        onSearchUsers={onSearchUsers}
      />
      {submitted && (isSearchSuccess || isPaginateSuccess) ? (
        <CirculationUsersTable
          isLoading={isSearching || isPaginating}
          pagination={pagination}
          statusChangeUserId={statusChangeUserId}
          users={usersWithStatusOverrides ?? []}
          onBlockUser={onBlockUser}
          onDeactivateOrDeleteUser={onDeactivateOrDeleteUser}
          onPaginate={onPaginateUsers}
          onUnblockUser={onUnblockUser}
          onUserDetailsClick={setSelectedUser}
        />
      ) : (
        <EuiEmptyPrompt
          actions={
            <EuiButton
              iconType='search'
              onClick={() => {
                onSearchUsers()
              }}
            >
              <FormattedMessage
                defaultMessage='Listar todos os usuários'
                id='circulation.users.controls-and-table.no-search.button'
              />
            </EuiButton>
          }
          body={
            <FormattedMessage
              defaultMessage='Utilize os controles acima para buscar usuários'
              id='circulation.users.table.no-search.body'
            />
          }
        />
      )}
      {selectedUser && (
        <CirculationUserDetailsFlyout
          disableIterateBackward={selectedUserIndex <= 0}
          disableIterateForward={
            usersWithStatusOverrides === undefined ||
            selectedUserIndex >= usersWithStatusOverrides.length - 1
          }
          user={selectedUser}
          onClose={() => setSelectedUser(null)}
          onIterateBackward={() => {
            if (usersWithStatusOverrides && selectedUserIndex > 0) {
              setSelectedUser(usersWithStatusOverrides[selectedUserIndex - 1])
            }
          }}
          onIterateForward={() => {
            if (
              usersWithStatusOverrides &&
              selectedUserIndex < usersWithStatusOverrides.length - 1
            ) {
              setSelectedUser(usersWithStatusOverrides[selectedUserIndex + 1])
            }
          }}
        />
      )}
      <EuiGlobalToastList
        dismissToast={removeToast}
        toastLifeTimeMs={5000}
        toasts={toasts}
      />
      {userPendingDelete && (
        <CirculationUserDeleteConfirmModal
          isLoading={isDeletingUser}
          user={userPendingDelete}
          onCancel={() => setUserPendingDelete(null)}
          onConfirm={onConfirmDeactivateOrDeleteUser}
        />
      )}
    </div>
  )
}

type CirculationUserDeleteConfirmModalProps = {
  user: User
  isLoading: boolean
  onCancel: () => void
  onConfirm: () => void
}

const CirculationUserDeleteConfirmModal = ({
  user,
  isLoading,
  onCancel,
  onConfirm,
}: CirculationUserDeleteConfirmModalProps) => {
  const modalTitleId = useGeneratedHtmlId()
  const isPermanentDelete = user.status === 'inactive'

  return (
    <EuiConfirmModal
      aria-labelledby={modalTitleId}
      buttonColor='danger'
      cancelButtonText={
        <FormattedMessage defaultMessage='Não' id='common.no' />
      }
      confirmButtonText={
        <FormattedMessage defaultMessage='Sim' id='common.yes' />
      }
      isLoading={isLoading}
      title={
        isPermanentDelete ? (
          <FormattedMessage
            defaultMessage='Excluir usuário'
            id='circulation.user.confirm_delete_record_title.forever'
          />
        ) : (
          <FormattedMessage
            defaultMessage='Marcar usuário como "inativo"'
            id='circulation.user.confirm_delete_record_title.inactive'
          />
        )
      }
      titleProps={{ id: modalTitleId }}
      onCancel={onCancel}
      onConfirm={onConfirm}
    >
      <p>
        {isPermanentDelete ? (
          <FormattedMessage
            defaultMessage='Você realmente deseja excluir este usuário?'
            id='circulation.user.confirm_delete_record_question.forever'
          />
        ) : (
          <FormattedMessage
            defaultMessage='Você realmente deseja marcar este usuário como "inativo"?'
            id='circulation.user.confirm_delete_record_question.inactive'
          />
        )}
      </p>
      <p>
        {isPermanentDelete ? (
          <FormattedMessage
            defaultMessage='Ele será excluído permanentemente do sistema e não poderá ser recuperado'
            id='circulation.user.confirm_delete_record.forever'
          />
        ) : (
          <FormattedMessage
            defaultMessage='Ele sairá da lista de pesquisas e só poderá ser encontrado através da "pesquisa avançada", de onde poderá ser excluído permanentemente ou recuperado'
            id='circulation.user.confirm_delete_record.inactive'
          />
        )}
      </p>
    </EuiConfirmModal>
  )
}

export default CirculationUsersControlsAndTable

function getUsers(
  usePaginatedResults: boolean,
  paginateResults: CirculationUsersSearchResponse | undefined,
  searchResults: CirculationUsersSearchResponse | undefined,
): User[] | undefined {
  if (usePaginatedResults && paginateResults !== undefined) {
    return paginateResults.success ? paginateResults.search.data : []
  }

  if (searchResults !== undefined) {
    return searchResults.success ? searchResults.search.data : []
  }

  return undefined
}
