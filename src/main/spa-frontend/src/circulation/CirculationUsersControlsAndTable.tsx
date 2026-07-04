import { useMemo, useState } from 'react'

import {
  useCirculationUsersPaginateMutation,
  useCirculationUsersSearchMutation,
} from '../api-helpers/circulation/hooks'

import CirculationUserDetailsFlyout from './CirculationUserDetailsFlyout'
import CirculationUsersControls from './CirculationUsersControls'
import CirculationUsersTable from './CirculationUsersTable'
import { toCirculationSearchPayload } from './lib'

import type { Pagination } from '@elastic/eui'

import type { User } from '../api-helpers/circulation/response-types'

import type { CirculationSearchControlConfig } from './types'

const CirculationUsersControlsAndTable = () => {
  const {
    mutate: searchUsers,
    isPending: isSearching,
    data: searchResults,
  } = useCirculationUsersSearchMutation()

  const {
    mutate: paginateUsers,
    isPending: isPaginating,
    data: paginateResults,
  } = useCirculationUsersPaginateMutation()

  const [usePaginatedResults, setUsePaginatedResults] = useState<boolean>(false)

  const [selectedUser, setSelectedUser] = useState<User | null>(null)

  const users = usePaginatedResults
    ? paginateResults?.search.data
    : searchResults?.search.data

  const selectedUserIndex =
    selectedUser && users
      ? users.findIndex((user) => user.id === selectedUser.id)
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
    searchUsers(toCirculationSearchPayload(searchConfig))
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
      <CirculationUsersTable
        isLoading={isSearching || isPaginating}
        pagination={pagination}
        users={users}
        onPaginate={onPaginateUsers}
        onUserDetailsClick={setSelectedUser}
      />
      {selectedUser && (
        <CirculationUserDetailsFlyout
          disableIterateBackward={selectedUserIndex <= 0}
          disableIterateForward={
            users === undefined || selectedUserIndex >= users.length - 1
          }
          user={selectedUser}
          onClose={() => setSelectedUser(null)}
          onIterateBackward={() => {
            if (users && selectedUserIndex > 0) {
              setSelectedUser(users[selectedUserIndex - 1])
            }
          }}
          onIterateForward={() => {
            if (users && selectedUserIndex < users.length - 1) {
              setSelectedUser(users[selectedUserIndex + 1])
            }
          }}
        />
      )}
    </div>
  )
}

export default CirculationUsersControlsAndTable
