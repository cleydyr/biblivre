import {
  EuiButtonEmpty,
  EuiDatePicker,
  EuiDatePickerRange,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFormRow,
  EuiSwitch,
} from '@elastic/eui'
import moment from 'moment'
import { FormattedMessage } from 'react-intl'

import { DAY_FIRST_DATE_FORMAT } from '../constants'
import { getEmptyDateRange } from '../lib/dates'

import type { FC } from 'react'

import type { CirculationUsersAdvancedSearchFilters } from './types'

type CirculationUsersAdvancedSearchFormProps = {
  filters: CirculationUsersAdvancedSearchFilters
  onFiltersChange: (value: CirculationUsersAdvancedSearchFilters) => void
}

const CirculationUsersAdvancedSearchForm: FC<
  CirculationUsersAdvancedSearchFormProps
> = ({ filters, onFiltersChange }) => {
  const {
    createdAtRange,
    modifiedAtRange,
    usersWithPendingFines,
    usersWithLateLendings,
    usersWhoHaveLoginAccess,
    usersWithoutUserCard,
    inactiveUsersOnly,
  } = filters

  const today = moment()

  return (
    <EuiFlexGroup direction='column' gutterSize='l'>
      <EuiFlexItem>
        <EuiFlexGroup alignItems='flexStart'>
          <EuiFlexItem grow={false}>
            <EuiFormRow
              label={
                <FormattedMessage
                  defaultMessage='Cadastrado entre'
                  id='search.common.registered_between'
                />
              }
            >
              <EuiDatePickerRange
                compressed
                endDateControl={
                  <EuiDatePicker
                    dateFormat={DAY_FIRST_DATE_FORMAT}
                    locale='pt-br'
                    maxDate={today}
                    selected={createdAtRange.to}
                    onChange={(date) => {
                      onFiltersChange({
                        ...filters,
                        createdAtRange: {
                          ...createdAtRange,
                          to: date,
                        },
                      })
                    }}
                  />
                }
                startDateControl={
                  <EuiDatePicker
                    dateFormat={DAY_FIRST_DATE_FORMAT}
                    locale='pt-br'
                    maxDate={today}
                    selected={createdAtRange.from}
                    onChange={(date) => {
                      onFiltersChange({
                        ...filters,
                        createdAtRange: {
                          ...createdAtRange,
                          from: date,
                        },
                      })
                    }}
                  />
                }
              />
            </EuiFormRow>
          </EuiFlexItem>
          <EuiFlexItem grow={false}>
            <EuiFormRow
              label={
                <FormattedMessage
                  defaultMessage='Alterado entre'
                  id='search.common.modified_between'
                />
              }
            >
              <EuiDatePickerRange
                compressed
                endDateControl={
                  <EuiDatePicker
                    dateFormat={DAY_FIRST_DATE_FORMAT}
                    locale='pt-br'
                    maxDate={today}
                    selected={modifiedAtRange.to}
                    onChange={(date) => {
                      onFiltersChange({
                        ...filters,
                        modifiedAtRange: {
                          ...modifiedAtRange,
                          to: date,
                        },
                      })
                    }}
                  />
                }
                startDateControl={
                  <EuiDatePicker
                    dateFormat={DAY_FIRST_DATE_FORMAT}
                    locale='pt-br'
                    maxDate={today}
                    selected={modifiedAtRange.from}
                    onChange={(date) => {
                      onFiltersChange({
                        ...filters,
                        modifiedAtRange: {
                          ...modifiedAtRange,
                          from: date,
                        },
                      })
                    }}
                  />
                }
              />
            </EuiFormRow>
          </EuiFlexItem>
        </EuiFlexGroup>
      </EuiFlexItem>
      <EuiFlexItem>
        <EuiFlexGroup direction='column'>
          <EuiFlexItem>
            <EuiSwitch
              compressed
              checked={usersWithPendingFines}
              label={
                <FormattedMessage
                  defaultMessage='Apenas usuários com multas pendentes'
                  id='search.common.only_pending_fines'
                />
              }
              onChange={() => {
                onFiltersChange({
                  ...filters,
                  usersWithPendingFines: !usersWithPendingFines,
                })
              }}
            />
          </EuiFlexItem>
          <EuiFlexItem>
            <EuiSwitch
              compressed
              checked={usersWithLateLendings}
              label={
                <FormattedMessage
                  defaultMessage='Apenas usuários com empréstimos em atraso'
                  id='search.common.only_overdue'
                />
              }
              onChange={() => {
                onFiltersChange({
                  ...filters,
                  usersWithLateLendings: !usersWithLateLendings,
                })
              }}
            />
          </EuiFlexItem>
          <EuiFlexItem>
            <EuiSwitch
              compressed
              checked={usersWhoHaveLoginAccess}
              itemRef='onlyLoginFilter'
              label={
                <FormattedMessage
                  defaultMessage='Apenas usuários com login de acesso ao Biblivre'
                  id='search.common.only_login'
                />
              }
              onChange={() => {
                onFiltersChange({
                  ...filters,
                  usersWhoHaveLoginAccess: !usersWhoHaveLoginAccess,
                })
              }}
            />
          </EuiFlexItem>
          <EuiFlexItem>
            <EuiSwitch
              compressed
              checked={usersWithoutUserCard}
              label={
                <FormattedMessage
                  defaultMessage='Apenas usuários com cartão de acesso'
                  id='search.common.only_access_card'
                />
              }
              onChange={() => {
                onFiltersChange({
                  ...filters,
                  usersWithoutUserCard: !usersWithoutUserCard,
                })
              }}
            />
          </EuiFlexItem>
          <EuiFlexItem>
            <EuiSwitch
              compressed
              checked={inactiveUsersOnly}
              label={
                <FormattedMessage
                  defaultMessage='Apenas usuários inativos'
                  id='search.common.only_inactive'
                />
              }
              onChange={() => {
                onFiltersChange({
                  ...filters,
                  inactiveUsersOnly: !inactiveUsersOnly,
                })
              }}
            />
          </EuiFlexItem>
        </EuiFlexGroup>
      </EuiFlexItem>
      <EuiFlexItem grow={false}>
        <div>
          <EuiButtonEmpty
            iconType='cross'
            size='s'
            onClick={() => {
              onFiltersChange({
                ...filters,
                createdAtRange: getEmptyDateRange(),
                modifiedAtRange: getEmptyDateRange(),
                usersWithPendingFines: false,
                usersWithLateLendings: false,
                usersWhoHaveLoginAccess: false,
                usersWithoutUserCard: false,
                inactiveUsersOnly: false,
              })
            }}
          >
            <FormattedMessage
              defaultMessage='Limpar filtros'
              id='search.common.clear_filters'
            />
          </EuiButtonEmpty>
        </div>
      </EuiFlexItem>
    </EuiFlexGroup>
  )
}

export default CirculationUsersAdvancedSearchForm
