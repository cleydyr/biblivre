import {
  EuiButtonIcon,
  EuiFieldText,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFormRow,
  EuiSelect,
  type EuiSelectOption,
} from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import { FIELDS } from '../api-helpers/search/constants'

import type {
  AdvancedQuery,
  HumanReadableQueryField,
  QueryOperator,
} from '../api-helpers/search/types'
import type { FC } from 'react'

export type AdvancedQueryFieldState = AdvancedQuery & {
  termFieldId: string
}

type Props = {
  query: AdvancedQueryFieldState
  onChange: (query: AdvancedQueryFieldState) => void
  onRemove: () => void
  order: number
}

const AdvancedBibliographicSearchControlsField: FC<Props> = ({
  query,
  onChange,
  onRemove,
  order,
}) => {
  const afterFirst = order > 0

  const fieldOptions = useFieldOptions()

  return (
    <EuiFlexGroup alignItems='flexEnd' gutterSize='s'>
      {afterFirst && (
        <EuiFlexItem grow={false}>
          <EuiFormRow
            label={
              <FormattedMessage
                defaultMessage='Operador'
                id='search.bibliographic.operator'
              />
            }
          >
            <EuiSelect
              options={operatorOptions}
              value={query.operator}
              onChange={(e) =>
                onChange({
                  ...query,
                  operator: e.target.value as QueryOperator,
                })
              }
            />
          </EuiFormRow>
        </EuiFlexItem>
      )}
      <EuiFlexItem grow={false}>
        <EuiFormRow
          label={
            <FormattedMessage
              defaultMessage='Campo'
              id='search.bibliographic.field'
            />
          }
        >
          <EuiSelect
            options={fieldOptions}
            value={query.field}
            onChange={(e) =>
              onChange({
                ...query,
                field: e.target.value as HumanReadableQueryField,
              })
            }
          />
        </EuiFormRow>
      </EuiFlexItem>
      <EuiFlexItem grow={false}>
        <EuiFormRow
          label={
            <FormattedMessage
              defaultMessage='Termo'
              id='search.bibliographic.term'
            />
          }
        >
          <EuiFieldText
            css={{
              minWidth: '28rem',
            }}
            value={query.query}
            onChange={(e) =>
              onChange({
                ...query,
                query: e.target.value,
              })
            }
          />
        </EuiFormRow>
      </EuiFlexItem>
      {afterFirst && (
        <EuiFlexItem grow={false}>
          <EuiButtonIcon
            aria-label='Remove search field'
            color='danger'
            iconType='trash'
            onClick={onRemove}
          />
        </EuiFlexItem>
      )}
    </EuiFlexGroup>
  )
}

function useFieldOptions(): EuiSelectOption[] {
  return [
    {
      value: FIELDS.ANY,
      text: (
        <FormattedMessage
          defaultMessage='Qualquer campo'
          id='search.bibliographic.any'
        />
      ),
    },

    {
      value: FIELDS.AUTHOR,
      text: (
        <FormattedMessage
          defaultMessage='Autor'
          id='search.bibliographic.author'
        />
      ),
    },
    {
      value: FIELDS.YEAR,
      text: (
        <FormattedMessage
          defaultMessage='Ano de publicação'
          id='search.bibliographic.publication_year'
        />
      ),
    },
    {
      value: FIELDS.TITLE,
      text: (
        <FormattedMessage
          defaultMessage='Título'
          id='search.bibliographic.title'
        />
      ),
    },
    {
      value: FIELDS.SUBJECT,
      text: (
        <FormattedMessage
          defaultMessage='Assunto'
          id='search.bibliographic.shelf_location'
        />
      ),
    },
    {
      value: FIELDS.ISBN,
      text: (
        <FormattedMessage
          defaultMessage='ISBN'
          id='search.bibliographic.isbn'
        />
      ),
    },
    {
      value: FIELDS.PUBLISHER,
      text: (
        <FormattedMessage
          defaultMessage='Editora'
          id='search.bibliographic.publisher'
        />
      ),
    },
    {
      value: FIELDS.SERIES,
      text: (
        <FormattedMessage
          defaultMessage='Série'
          id='search.bibliographic.series'
        />
      ),
    },
  ]
}

const operatorOptions: EuiSelectOption[] = [
  {
    value: 'AND',
    text: (
      <FormattedMessage defaultMessage='e' id='search_query_operator.and' />
    ),
  },
  {
    value: 'OR',
    text: (
      <FormattedMessage defaultMessage='ou' id='search_query_operator.or' />
    ),
  },
  {
    value: 'AND_NOT',
    text: (
      <FormattedMessage
        defaultMessage='e não'
        id='search_query_operator.and_not'
      />
    ),
  },
] as const

export default AdvancedBibliographicSearchControlsField
