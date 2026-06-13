import {
  EuiButtonIcon,
  EuiFieldText,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFormRow,
  EuiSpacer,
  EuiToolTip,
} from '@elastic/eui'
import { useMemo } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'

import { useBibliographicIndexingGroups } from '../../api-helpers/indexing-groups/hooks'
import { FIELDS } from '../../api-helpers/search/constants'
import TypedEuiSelect from '../../components/TypedEuiSelect'
import { getLegacyBibliographicIndexingGroupTranslation } from '../../legacy_translations/lib'

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

  const { fieldOptions, isLoading: isFieldOptionsLoading } = useFieldOptions()

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
                disabled={isFieldOptionsLoading}
                options={fieldOptions}
                value={term?.field ?? FIELDS.TITLE}
                onChange={(e) =>
                  onChange({
                    ...term,
                    field: e.target.value,
                  })
                }
                isLoading={isFieldOptionsLoading}
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

function useFieldOptions(): {
  fieldOptions: TypedEuiSelectOption<EncodedTextQueryField>[]
  isLoading: boolean
} {
  const { data: indexingGroups = [], isLoading } =
    useBibliographicIndexingGroups()

  const fieldOptions = useMemo(
    () =>
      indexingGroups.map((group) => ({
        value: String(group.id) as EncodedTextQueryField,
        text: getLegacyBibliographicIndexingGroupTranslation(
          group.translationKey,
        ),
      })),
    [indexingGroups],
  )

  return { fieldOptions, isLoading }
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
