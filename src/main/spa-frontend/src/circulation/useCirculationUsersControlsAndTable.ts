import { useCallback, useMemo, useState } from 'react'

import {
  useBlockCirculationUserMutation,
  useCirculationUsersPaginateMutation,
  useCirculationUsersSearchMutation,
  useDeleteCirculationUserMutation,
  useUnblockCirculationUserMutation,
} from '../api-helpers/circulation/hooks'
import useLatch from '../hooks/useLatch'
import { unshiftOrReplaceWithId } from '../lib/arrays'
import { useToasts } from '../toasts/useToasts'

import { toCirculationSearchPayload } from './lib'

import type { Pagination } from '@elastic/eui'

import type {
  CirculationUsersSearchResponse,
  User,
} from '../api-helpers/circulation/response-types'

import type { CirculationSearchControlConfig } from './types'

export type CirculationUserFormMode =
  null | { mode: 'create' } | { mode: 'edit'; user: User }

const useCirculationUsersControlsAndTable = () => {
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
  const [userFormMode, setUserFormMode] =
    useState<CirculationUserFormMode>(null)
  const [userPendingDelete, setUserPendingDelete] = useState<User | null>(null)
  const [userStatusOverrides, setUserStatusOverrides] = useState<
    Record<number, User['status']>
  >({})

  const { value: submitted, latch: latchSubmitted } = useLatch()

  const { showToast } = useToasts()

  const showStatusChangeToast = useCallback(
    (response: { success: boolean; message?: string }, userId: number) => {
      showToast({
        id: `user-status-change-${userId}-${Date.now()}`,
        title: response.message ?? '',
        color: response.success ? 'success' : 'warning',
        iconType: response.success ? 'check' : 'alert',
      })
    },
    [showToast],
  )

  const users: User[] | undefined = getUsers(
    usePaginatedResults,
    paginateResults,
    searchResults,
  )

  const [removedUserIds, setRemovedUserIds] = useState<number[]>([])
  const [savedUsersById, setSavedUsersById] = useState<Record<number, User>>({})
  const [createdUsers, setCreatedUsers] = useState<User[]>([])

  const usersWithStatusOverrides = useMemo(() => {
    if (users === undefined) {
      return undefined
    }

    const mergedUsers = [...users]

    for (const createdUser of createdUsers) {
      if (!mergedUsers.some((user) => user.id === createdUser.id)) {
        mergedUsers.unshift(createdUser)
      }
    }

    return mergedUsers
      .filter((user) => !removedUserIds.includes(user.id))
      .map((user) => {
        const savedUser =
          user.id in savedUsersById ? savedUsersById[user.id] : undefined
        const status =
          user.id in userStatusOverrides
            ? userStatusOverrides[user.id]
            : user.status

        if (savedUser) {
          return { ...savedUser, status }
        }

        if (user.id in userStatusOverrides) {
          return { ...user, status }
        }

        return user
      })
  }, [users, userStatusOverrides, removedUserIds, savedUsersById, createdUsers])

  const statusChangeUserId = isBlockingUser
    ? blockingUserId
    : isUnblockingUser
      ? unblockingUserId
      : isDeletingUser
        ? deletingUserId
        : null

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

  const [searchConfig, setSearchConfig] =
    useState<CirculationSearchControlConfig>({
      query: '',
      searchField: '',
      isAdvancedSearch: false,
    })

  const onSearchUsers = () => {
    setUsePaginatedResults(false)
    setUserStatusOverrides({})
    setRemovedUserIds([])
    setCreatedUsers([])
    setSavedUsersById({})
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

  const onCreateUserClick = useCallback(() => {
    setSelectedUser(null)
    setUserFormMode({ mode: 'create' })
  }, [])

  const onEditUserClick = useCallback((user: User) => {
    setSelectedUser(null)
    setUserFormMode({ mode: 'edit', user })
  }, [])

  const onCloseUserForm = useCallback(() => {
    setUserFormMode(null)
  }, [])

  const onUserSaved = useCallback((user: User) => {
    setSavedUsersById((currentSavedUsers) => ({
      ...currentSavedUsers,
      [user.id]: user,
    }))

    setCreatedUsers((currentCreatedUsers) => {
      return unshiftOrReplaceWithId(currentCreatedUsers, user)
    })
    setSelectedUser(user)
  }, [])

  return {
    isDeletingUser,
    isPaginateSuccess,
    isPaginating,
    isSearchSuccess,
    isSearching,
    onBlockUser,
    onCloseUserForm,
    onConfirmDeactivateOrDeleteUser,
    onCreateUserClick,
    onDeactivateOrDeleteUser,
    onEditUserClick,
    onPaginateUsers,
    onSearchUsers,
    onUnblockUser,
    onUserSaved,
    pagination,
    searchConfig,
    selectedUser,
    selectedUserIndex,
    setSearchConfig,
    setSelectedUser,
    setUserPendingDelete,
    statusChangeUserId,
    submitted,
    userFormMode,
    userPendingDelete,
    usersWithStatusOverrides,
  }
}

export type UseCirculationUsersControlsAndTableReturn = ReturnType<
  typeof useCirculationUsersControlsAndTable
>

export default useCirculationUsersControlsAndTable

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
