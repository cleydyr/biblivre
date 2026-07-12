import {
  EuiButton,
  EuiFieldSearch,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFormRow,
  EuiPanel,
  EuiSelect,
  EuiSpacer,
  EuiSwitch,
} from '@elastic/eui'
import { useMemo } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'

import { useUserSearchableFields } from '../api-helpers/user-searchable-fields/hooks'
import {
  getLegacyTranslation,
  getLegacyUserFieldTranslation,
} from '../legacy_translations/lib'
import { getEmptyDateRange } from '../lib/dates'

import CirculationUsersAdvancedSearchForm from './CirculationUsersAdvancedSearchForm'

import type { FC } from 'react'

import type { TypedEuiSelectOption } from '../components/TypedEuiSelect'

import type { CirculationSearchControlConfig } from './types'

type CirculationUsersControlsProps = {
  isLoading: boolean
  searchConfig: CirculationSearchControlConfig
  onSearchConfigChange: (searchConfig: CirculationSearchControlConfig) => void
  onSearchUsers: (payload: CirculationSearchControlConfig) => void
}

const CirculationUsersControls: FC<CirculationUsersControlsProps> = ({
  isLoading,
  searchConfig,
  onSearchUsers,
  onSearchConfigChange,
}) => {
  const { formatMessage } = useIntl()

  const { query, searchField, isAdvancedSearch } = searchConfig

  const { data: searchableFields = [], isLoading: isFieldsLoading } =
    useUserSearchableFields()

  const fieldOptions = useMemo(
    (): TypedEuiSelectOption<string>[] => [
      {
        value: '',
        text: getLegacyTranslation('search.user.name_or_id'),
      },
      ...searchableFields.map((field) => ({
        value: field.key,
        text: getLegacyUserFieldTranslation(field.key),
      })),
    ],
    [searchableFields],
  )

  return (
    <EuiPanel hasBorder paddingSize='l'>
      <EuiFlexGroup direction='column'>
        <EuiFlexItem>
          <EuiFlexGroup alignItems='flexEnd' gutterSize='m'>
            <EuiFlexItem>
              <EuiFieldSearch
                compressed
                isLoading={isLoading}
                placeholder={formatMessage({
                  defaultMessage: 'Pesquisar por nome, email, telefone, etc.',
                  id: 'circulation.users.search.placeholder',
                })}
                size={32}
                onChange={(e) => {
                  onSearchConfigChange({
                    ...searchConfig,
                    query: e.target.value,
                  })
                }}
              />
            </EuiFlexItem>
            <EuiFlexItem grow={false}>
              <EuiFormRow
                label={
                  <FormattedMessage
                    defaultMessage='Campo de busca'
                    id='circulation.users.search.field'
                  />
                }
              >
                <EuiSelect
                  compressed
                  disabled={isLoading || isFieldsLoading}
                  isLoading={isFieldsLoading}
                  options={fieldOptions}
                  value={searchField}
                  onChange={(e) => {
                    onSearchConfigChange({
                      ...searchConfig,
                      searchField: e.target.value,
                    })
                  }}
                />
              </EuiFormRow>
            </EuiFlexItem>
            <EuiFlexItem grow={false}>
              <EuiButton
                isLoading={isLoading}
                size='s'
                onClick={() => onSearchUsers(searchConfig)}
              >
                <FormattedMessage
                  defaultMessage='Pesquisar'
                  id='circulation.users.search.button'
                />
              </EuiButton>
            </EuiFlexItem>
            <EuiFlexItem grow={false}>
              <EuiFlexGroup direction='column' gutterSize='none'>
                <EuiSwitch
                  checked={isAdvancedSearch}
                  label={
                    <FormattedMessage
                      defaultMessage='Pesquisa avançada'
                      id='circulation.users.advanced.search.label'
                    />
                  }
                  onChange={() => {
                    if (isAdvancedSearch) {
                      onSearchConfigChange({
                        query,
                        searchField,
                        isAdvancedSearch: false,
                      })

                      return
                    }

                    onSearchConfigChange({
                      query,
                      searchField,
                      isAdvancedSearch: true,
                      filters: {
                        createdAtRange: getEmptyDateRange(),
                        modifiedAtRange: getEmptyDateRange(),
                        usersWithPendingFines: false,
                        usersWithLateLendings: false,
                        usersWhoHaveLoginAccess: false,
                        usersWithoutUserCard: false,
                        inactiveUsersOnly: false,
                      },
                    })
                  }}
                />
                <EuiSpacer size='xs' />
              </EuiFlexGroup>
            </EuiFlexItem>
          </EuiFlexGroup>
        </EuiFlexItem>
        {isAdvancedSearch && (
          <CirculationUsersAdvancedSearchForm
            filters={searchConfig.filters}
            onFiltersChange={(value) => {
              onSearchConfigChange({
                ...searchConfig,
                filters: value,
              })
            }}
          />
        )}
      </EuiFlexGroup>
    </EuiPanel>
  )
}

export default CirculationUsersControls
