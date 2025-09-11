import {
  EuiButton,
  EuiButtonEmpty,
  EuiFlexGroup,
  EuiFlexItem,
} from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import useMap from '../../hooks/useMap'

import AdvancedBibliographicSearchControlsField from './AdvancedBibliographicSearchControlsField'
import { generateTermField, getValidQueries } from './lib'

import type { FC } from 'react'

import type { AdvancedQueryTerm } from '../../api-helpers/search/types'

import type { UUID } from './types'

type Props = {
  onQuerySubmited: (terms: AdvancedQueryTerm[] | undefined) => void
  isLoading: boolean
}

const AdvancedBibliographicSearchControls: FC<Props> = ({
  onQuerySubmited,
  isLoading,
}) => {
  const termFieldsMap = useMap<UUID, AdvancedQueryTerm>([generateTermField()])

  const addTerm = () => {
    termFieldsMap.set(...generateTermField())
  }

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
            <EuiButtonEmpty iconType='plusInCircle' size='s' onClick={addTerm}>
              <FormattedMessage
                defaultMessage='Adicionar termo'
                id='search.bibliographic.add-term'
              />
            </EuiButtonEmpty>
          </EuiFlexItem>
        </EuiFlexGroup>
      </EuiFlexItem>
      <EuiFlexGroup justifyContent='flexEnd'>
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
            onQuerySubmited(getValidQueries(termFieldsMap))
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
