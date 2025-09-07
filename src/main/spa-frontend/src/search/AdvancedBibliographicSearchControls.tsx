import { EuiButton, EuiFlexGroup, EuiFlexItem } from '@elastic/eui'
import { omit } from 'es-toolkit'
import { useState } from 'react'
import { FormattedMessage } from 'react-intl'

import AdvancedBibliographicSearchControlsField, {
  type AdvancedQueryFieldState,
} from './AdvancedBibliographicSearchControlsField'

import type { AdvancedQuery } from '../api-helpers/search/types'
import type { FC } from 'react'

type Props = {
  onQuerySubmited: (query: AdvancedQuery[]) => void
  isLoading: boolean
}

const AdvancedBibliographicSearchControls: FC<Props> = ({
  onQuerySubmited,
  isLoading,
}) => {
  const [queries, setQueries] = useState<AdvancedQueryFieldState[]>([
    {
      termFieldId: crypto.randomUUID(),
      query: '',
      operator: 'AND',
      field: 'ANY',
    },
  ])

  const addQuery = () => {
    setQueries([
      ...queries,
      {
        termFieldId: crypto.randomUUID(),
        query: '',
        operator: 'AND',
        field: 'ANY',
      },
    ])
  }

  const removeQuery = (termFieldId: string) => {
    if (queries.length > 1) {
      setQueries(queries.filter((q) => q.termFieldId !== termFieldId))
    }
  }

  return (
    <EuiFlexGroup direction='column' gutterSize='m'>
      {queries.map((query, index) => (
        <EuiFlexItem key={query.termFieldId}>
          <AdvancedBibliographicSearchControlsField
            order={index}
            query={query}
            onChange={(updatedQuery: AdvancedQueryFieldState) =>
              setQueries(
                queries.map((q) =>
                  q.termFieldId === updatedQuery.termFieldId ? updatedQuery : q
                )
              )
            }
            onRemove={() => removeQuery(query.termFieldId)}
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
                onQuerySubmited(
                  queries.map((query) => omit(query, ['termFieldId']))
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
