import {
  EuiButtonIcon,
  EuiFieldText,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFormRow,
  EuiSelect,
  EuiSpacer,
  EuiToolTip,
} from '@elastic/eui'
import { FormattedMessage, useIntl } from 'react-intl'

import { FIELDS } from '../../api-helpers/search/constants'

import type { EuiSelectOption } from '@elastic/eui'
import type { ChangeEvent, ComponentProps, EventHandler, FC } from 'react'
import type { Omit } from 'utility-types'

import type {
  AdvancedQueryTerm,
  EncodedQueryField,
  QueryOperator,
} from '../../api-helpers/search/types'

type Props = {
  term: AdvancedQueryTerm
  onChange: (term: AdvancedQueryTerm) => void
  onRemove: () => void
  order: number
}

const AdvancedBibliographicSearchControlsField: FC<Props> = ({
  term,
  onChange,
  onRemove,
  order,
}) => {
  const { formatMessage } = useIntl()

  const afterFirst = order > 0

  const fieldOptions = useFieldOptions()

  return (
    <EuiFlexGroup>
      <EuiFlexItem grow={8}>
        <EuiFlexGroup>
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
                <TypedEuiSelect<QueryOperator>
                  options={operatorOptions}
                  value={term?.operator || 'AND'}
                  onChange={(e) => {
                    onChange({
                      ...term,
                      operator: e.target.value,
                    })
                  }}
                />
              </EuiFormRow>
            </EuiFlexItem>
          )}

          <EuiFlexItem>
            <EuiFormRow
              helpText={
                <FormattedMessage
                  defaultMessage='Termos vazios não serão nos resultados da busca'
                  id='search.bibliographic.term_help'
                />
              }
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
                value={term.query}
                onChange={(e) =>
                  onChange({
                    ...term,
                    query: e.target.value,
                  })
                }
              />
            </EuiFormRow>
          </EuiFlexItem>
        </EuiFlexGroup>
      </EuiFlexItem>
      <EuiFlexItem grow={5}>
        <EuiFlexGroup alignItems='center'>
          <EuiFlexItem grow={false}>
            <EuiFormRow
              label={
                <FormattedMessage
                  defaultMessage='Campo'
                  id='search.bibliographic.field'
                />
              }
            >
              <TypedEuiSelect<EncodedQueryField>
                options={fieldOptions}
                value={term?.field ?? FIELDS.TITLE}
                onChange={(e) =>
                  onChange({
                    ...term,
                    field: e.target.value,
                  })
                }
              />
            </EuiFormRow>
          </EuiFlexItem>
          {afterFirst && (
            <EuiFlexItem grow={false}>
              <EuiSpacer size='m' />
              {/* to compensate for the form row label */}
              <EuiToolTip
                content={formatMessage({
                  defaultMessage: 'Remover termo',
                  id: 'search.bibliographic.remove_field',
                })}
                position='top'
              >
                <EuiButtonIcon
                  aria-label={formatMessage({
                    defaultMessage: 'Remover termo',
                    id: 'search.bibliographic.remove_field',
                  })}
                  color='danger'
                  iconType='trash'
                  onClick={onRemove}
                />
              </EuiToolTip>
            </EuiFlexItem>
          )}
        </EuiFlexGroup>
      </EuiFlexItem>
    </EuiFlexGroup>
  )
}

function useFieldOptions(): TypedEuiSelectOption<EncodedQueryField>[] {
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

type TypedEuiSelectOption<T extends string> = Omit<EuiSelectOption, 'value'> & {
  value: T
}

function TypedEuiSelect<T extends string>(
  props: Omit<
    ComponentProps<typeof EuiSelect>,
    'value' | 'options' | 'onChange'
  > & {
    value: T
    options: TypedEuiSelectOption<T>[]
    onChange: EventHandler<
      ChangeEvent<Omit<HTMLSelectElement, 'value'> & { value: T }>
    >
  }
) {
  return <EuiSelect {...props} />
}

const operatorOptions: TypedEuiSelectOption<QueryOperator>[] = [
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
