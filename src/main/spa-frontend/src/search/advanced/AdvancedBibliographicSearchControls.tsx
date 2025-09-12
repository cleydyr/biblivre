import {
  EuiButton,
  EuiButtonEmpty,
  EuiDatePicker,
  EuiDatePickerRange,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFormRow,
} from '@elastic/eui'
import moment from 'moment'
import { type FC, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import useMap from '../../hooks/useMap'

import AdvancedBibliographicSearchControlsField from './AdvancedBibliographicSearchControlsField'
import { generateTermField, getValidQueries } from './lib'

import type {
  AdvancedQueryTerm,
  AdvancedTextQueryTerm,
} from '../../api-helpers/search/types'

import type { DateRange, UUID } from './types'

type Props = {
  onQuerySubmited: (terms: AdvancedQueryTerm[] | undefined) => void
  isLoading: boolean
}

const AdvancedBibliographicSearchControls: FC<Props> = ({
  onQuerySubmited,
  isLoading,
}) => {
  const termFieldsMap = useMap<UUID, AdvancedTextQueryTerm>([
    generateTermField(),
  ])

  const [createdFilter, setCreatedFilter] = useState<DateRange>({
    from: null,
    to: null,
  })

  const [modifiedFilter, setModifiedFilter] = useState<DateRange>({
    from: null,
    to: null,
  })

  const today = moment()

  return (
    <EuiFlexGroup direction='column' gutterSize='l'>
      <EuiFlexGroup direction='column' gutterSize='m'>
        {[...termFieldsMap.entries()].map(([termFieldId, term], index) => (
          <EuiFlexItem key={termFieldId}>
            <AdvancedBibliographicSearchControlsField
              order={index}
              term={term}
              onChange={(term) => termFieldsMap.set(termFieldId, term)}
              onRemove={() => termFieldsMap.delete(termFieldId)}
            />
          </EuiFlexItem>
        ))}
      </EuiFlexGroup>
      <EuiFlexItem>
        <EuiFlexGroup gutterSize='s'>
          <EuiFlexItem grow={false}>
            <EuiButtonEmpty
              iconType='plusInCircle'
              size='s'
              onClick={() => {
                termFieldsMap.set(...generateTermField())
              }}
            >
              <FormattedMessage
                defaultMessage='Adicionar termo'
                id='search.bibliographic.add-term'
              />
            </EuiButtonEmpty>
          </EuiFlexItem>
        </EuiFlexGroup>
      </EuiFlexItem>
      <EuiFlexGroup justifyContent='flexEnd'>
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
                  setCreatedFilter((prev) => ({
                    ...prev,
                    to: date,
                  }))
                }}
              />
            }
            startDateControl={
              <EuiDatePicker
                compressed
                maxDate={today}
                selected={createdFilter?.from}
                onChange={(date) => {
                  setCreatedFilter((prev) => ({
                    ...prev,
                    from: date,
                  }))
                }}
              />
            }
          />
        </EuiFormRow>
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
                  setModifiedFilter((prev) => ({
                    ...prev,
                    to: date,
                  }))
                }}
              />
            }
            startDateControl={
              <EuiDatePicker
                compressed
                maxDate={today}
                selected={modifiedFilter?.from}
                onChange={(date) => {
                  setModifiedFilter((prev) => ({
                    ...prev,
                    from: date,
                  }))
                }}
              />
            }
          />
        </EuiFormRow>

        <EuiButtonEmpty
          color='neutral'
          iconType='cross'
          onClick={() => {
            termFieldsMap.clear()
            termFieldsMap.set(...generateTermField())
          }}
        >
          <FormattedMessage
            defaultMessage='Limpar todos os campos'
            id='search.bibliographic.clear-all'
          />
        </EuiButtonEmpty>
        <EuiButton
          fill
          iconType='search'
          isLoading={isLoading}
          onClick={() => {
            onQuerySubmited(
              getValidQueries(termFieldsMap, createdFilter, modifiedFilter)
            )
          }}
        >
          <FormattedMessage
            defaultMessage='Pesquisar'
            id='search.bibliographic.search'
          />
        </EuiButton>
      </EuiFlexGroup>
    </EuiFlexGroup>
  )
}

export default AdvancedBibliographicSearchControls
