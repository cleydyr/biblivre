import {
  EuiDatePicker,
  EuiDatePickerRange,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFormRow,
} from '@elastic/eui'
import moment from 'moment'
import { FormattedMessage } from 'react-intl'

import type { FC } from 'react'

import type { DateRange } from './types'

type Props = {
  createdFilter: DateRange
  modifiedFilter: DateRange
  onCreatedFilterChanged: (date: DateRange) => void
  onModifiedFilterChanged: (date: DateRange) => void
}

const AdvancedBibliographicSearchDateFilters: FC<Props> = ({
  createdFilter,
  modifiedFilter,
  onModifiedFilterChanged,
  onCreatedFilterChanged,
}) => {
  const today = moment()

  return (
    <EuiFlexGroup alignItems='flexStart'>
      <EuiFlexItem grow={false}>
        <EuiFormRow
          label={
            <FormattedMessage
              defaultMessage='Data de criação'
              id='search.bibliographic.created-date'
            />
          }
        >
          <EuiDatePickerRange
            compressed
            endDateControl={
              <EuiDatePicker
                compressed
                maxDate={today}
                selected={createdFilter?.to}
                onChange={(date) => {
                  onCreatedFilterChanged({
                    ...createdFilter,
                    to: date,
                  })
                }}
              />
            }
            startDateControl={
              <EuiDatePicker
                compressed
                maxDate={today}
                selected={createdFilter?.from}
                onChange={(date) => {
                  onCreatedFilterChanged({
                    ...modifiedFilter,
                    from: date,
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
              defaultMessage='Data de modificação'
              id='search.bibliographic.modified-date'
            />
          }
        >
          <EuiDatePickerRange
            compressed
            endDateControl={
              <EuiDatePicker
                maxDate={today}
                selected={modifiedFilter?.to}
                onChange={(date) => {
                  onModifiedFilterChanged({
                    ...modifiedFilter,
                    to: date,
                  })
                }}
              />
            }
            startDateControl={
              <EuiDatePicker
                compressed
                maxDate={today}
                selected={modifiedFilter?.from}
                onChange={(date) => {
                  onModifiedFilterChanged({
                    ...modifiedFilter,
                    from: date,
                  })
                }}
              />
            }
          />
        </EuiFormRow>
      </EuiFlexItem>
    </EuiFlexGroup>
  )
}

export default AdvancedBibliographicSearchDateFilters
