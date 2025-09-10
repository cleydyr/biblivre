import { EuiButton, EuiFlexGroup, EuiFlexItem } from '@elastic/eui'
import { type FC, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import { FIELDS } from '../api-helpers/search/constants'

import AdvancedBibliographicSearchControlsField from './AdvancedBibliographicSearchControlsField'

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
}

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

class ObservableMap<K, V> extends Map<K, V> {
  observer: (map: Map<K, V>) => void

  constructor(initialMap: Map<K, V>, observer: (map: Map<K, V>) => void) {
    super()

    this.observer = observer

    initialMap.forEach((value, key) => {
      super.set(key, value)
    })
  }

  set(key: K, value: V): this {
    const result = super.set(key, value)

    this.observer(this)

    return result
  }

  clear(): void {
    super.clear()

    this.observer(this)
  }

  delete(key: K): boolean {
    const result = super.delete(key)

    this.observer(this)

    return result
  }
}

function useMap<K, V>(
  args: ConstructorParameters<typeof Map<K, V>>[0] = []
): InstanceType<typeof Map<K, V>> {
  const [map, setMap] = useState(new Map<K, V>(args))

  return new ObservableMap<K, V>(map, setMap)
}
