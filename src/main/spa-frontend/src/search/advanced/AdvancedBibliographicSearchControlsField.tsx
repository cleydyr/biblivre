import {
  EuiButtonIcon,
  EuiFieldText,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFormRow,
  EuiSpacer,
  EuiToolTip,
} from '@elastic/eui'
import { FormattedMessage, useIntl } from 'react-intl'

import { FIELDS } from '../../api-helpers/search/constants'
import TypedEuiSelect from '../../components/TypedEuiSelect'

import type { FC } from 'react'

import type {
  AdvancedTextQueryTerm,
  EncodedTextQueryField,
  QueryOperator,
} from '../../api-helpers/search/types'
import type { TypedEuiSelectOption } from '../../components/TypedEuiSelect'

type Props = {
  term: AdvancedTextQueryTerm
  onChange: (term: AdvancedTextQueryTerm) => void
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
                  minWidth: '20rem',
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
        <EuiFlexGroup>
          <EuiFlexItem grow={false}>
            <EuiFormRow
              label={
                <FormattedMessage
                  defaultMessage='Campo'
                  id='search.bibliographic.field'
                />
              }
            >
              <TypedEuiSelect<EncodedTextQueryField>
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
              <EuiFlexGroup alignItems='center'>
                <EuiSpacer size='l' />
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
              </EuiFlexGroup>
            </EuiFlexItem>
          )}
        </EuiFlexGroup>
      </EuiFlexItem>
    </EuiFlexGroup>
  )
}

function useFieldOptions(): TypedEuiSelectOption<EncodedTextQueryField>[] {
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
