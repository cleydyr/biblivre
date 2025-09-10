import { EuiButton, EuiFlexGroup, EuiFlexItem } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import useMap from '../../hooks/useMap'

import AdvancedBibliographicSearchControlsField from './AdvancedBibliographicSearchControlsField'
import { DUMMY_ADVANCED_QUERY_TERM } from './constants'
import { getValidQueries } from './lib'

import type { FC } from 'react'

import type { AdvancedQueryTerm } from '../../api-helpers/search/types'

type Props = {
  onQuerySubmited: (terms: AdvancedQueryTerm[] | undefined) => void
  isLoading: boolean
}

export type UUID = ReturnType<(typeof crypto)['randomUUID']>

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

  return (
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
                onQuerySubmited(getValidQueries(termFieldsMap))
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
