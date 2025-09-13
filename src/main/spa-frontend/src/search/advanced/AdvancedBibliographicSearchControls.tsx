import {
  EuiButton,
  EuiButtonEmpty,
  EuiFlexGroup,
  EuiFlexItem,
} from '@elastic/eui'
import { type FC, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import useMap from '../../hooks/useMap'

import AdvancedBibliographicSearchControlsField from './AdvancedBibliographicSearchControlsField'
import AdvancedBibliographicSearchDateFilters from './AdvancedBibliographicSearchDateFilters'
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
      <EuiFlexGroup alignItems='flexEnd' justifyContent='flexEnd'>
        <AdvancedBibliographicSearchDateFilters
          createdFilter={createdFilter}
          modifiedFilter={modifiedFilter}
          onCreatedFilterChanged={setCreatedFilter}
          onModifiedFilterChanged={setModifiedFilter}
        />
        <EuiFlexItem grow={false}>
          <EuiButtonEmpty
            color='neutral'
            iconType='cross'
            onClick={() => {
              termFieldsMap.clear()
              termFieldsMap.set(...generateTermField())
            }}
          >
            <FormattedMessage
              defaultMessage='Limpar campos'
              id='search.bibliographic.clear-all'
            />
          </EuiButtonEmpty>
        </EuiFlexItem>
        <EuiFlexItem>
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
        </EuiFlexItem>
      </EuiFlexGroup>
    </EuiFlexGroup>
  )
}

export default AdvancedBibliographicSearchControls
