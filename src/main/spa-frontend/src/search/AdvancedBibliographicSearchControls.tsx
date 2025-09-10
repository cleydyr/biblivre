import { EuiButton, EuiFlexGroup, EuiFlexItem } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import { FIELDS } from '../api-helpers/search/constants'
import useMap from '../hooks/useMap'

import AdvancedBibliographicSearchControlsField from './AdvancedBibliographicSearchControlsField'

import type { FC } from 'react'

import type { AdvancedQueryTerm } from '../api-helpers/search/types'

type Props = {
  onQuerySubmited: (terms: AdvancedQueryTerm[] | undefined) => void
  isLoading: boolean
}

type UUID = ReturnType<(typeof crypto)['randomUUID']>

const DUMMY_ADVANCED_QUERY_TERM: AdvancedQueryTerm = {
  field: FIELDS.ANY,
  operator: 'AND',
  query: '',
} as const

const AdvancedBibliographicSearchControls: FC<Props> = ({
  onQuerySubmited,
  isLoading,
}) => {
  const termFieldsMap = useMap<UUID, AdvancedQueryTerm>([
    [crypto.randomUUID(), DUMMY_ADVANCED_QUERY_TERM],
  ])

  const addQuery = () => {
    termFieldsMap.set(crypto.randomUUID(), DUMMY_ADVANCED_QUERY_TERM)
  }

  const removeQuery = (termFieldId: UUID) => {
    termFieldsMap.delete(termFieldId)
  }

  return (
    <EuiFlexGroup direction='column' gutterSize='m'>
      {[...termFieldsMap.entries()].map(([termFieldId, term], index) => (
        <EuiFlexItem key={termFieldId}>
          <AdvancedBibliographicSearchControlsField
            order={index}
            term={term}
            onChange={(term) => termFieldsMap.set(termFieldId, term)}
            onRemove={() => removeQuery(termFieldId)}
          />
        </EuiFlexItem>
      ))}
      <EuiFlexItem>
        <EuiFlexGroup gutterSize='s'>
          <EuiFlexItem grow={false}>
            <EuiButton size='s' onClick={addQuery}>
              <FormattedMessage
                defaultMessage='Adicionar campo'
                id='search.bibliographic.add-field'
              />
            </EuiButton>
          </EuiFlexItem>
          <EuiFlexItem grow={false}>
            <EuiButton
              fill
              isLoading={isLoading}
              onClick={() => {
                const validQueryTerms = [...termFieldsMap.entries()]
                  .filter(hasValidQuery)
                  .map(([, term]) => term)

                onQuerySubmited(
                  validQueryTerms.length > 0 ? validQueryTerms : undefined
                )
              }}
            >
              <FormattedMessage
                defaultMessage='Pesquisar'
                id='search.bibliographic.search'
              />
            </EuiButton>
          </EuiFlexItem>
        </EuiFlexGroup>
      </EuiFlexItem>
    </EuiFlexGroup>
  )
}

export default AdvancedBibliographicSearchControls

function hasValidQuery([_, term]: [UUID, AdvancedQueryTerm]): boolean {
  return term.query !== ''
}
