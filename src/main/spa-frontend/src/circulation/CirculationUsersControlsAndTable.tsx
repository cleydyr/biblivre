import {
  EuiButton,
  EuiConfirmModal,
  EuiEmptyPrompt,
  useGeneratedHtmlId,
} from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import CirculationUserDetailsFlyout from './CirculationUserDetailsFlyout'
import CirculationUserFormFlyout from './CirculationUserFormFlyout'
import CirculationUsersControls from './CirculationUsersControls'
import CirculationUsersTable from './CirculationUsersTable'
import useCirculationUsersControlsAndTable from './useCirculationUsersControlsAndTable'

import type { User } from '../api-helpers/circulation/response-types'

const CirculationUsersControlsAndTable = () => {
  const {
    isDeletingUser,
    isPaginateSuccess,
    isPaginating,
    isSearchSuccess,
    isSearching,
    onBlockUser,
    onCloseUserForm,
    onConfirmDeactivateOrDeleteUser,
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
  } = useCirculationUsersControlsAndTable()

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
          onEditUser={onEditUserClick}
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
          onEditUserClick={() => onEditUserClick(selectedUser)}
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
      {userFormMode?.mode === 'create' ? (
        <CirculationUserFormFlyout
          mode='create'
          onClose={onCloseUserForm}
          onSaved={onUserSaved}
        />
      ) : null}
      {userFormMode?.mode === 'edit' ? (
        <CirculationUserFormFlyout
          mode='edit'
          user={userFormMode.user}
          onClose={onCloseUserForm}
          onSaved={onUserSaved}
        />
      ) : null}
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
