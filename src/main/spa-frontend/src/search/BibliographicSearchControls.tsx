import {
  EuiFlexGroup,
  EuiFlexItem,
  EuiHorizontalRule,
  EuiSpacer,
  EuiSwitch,
  EuiTitle,
} from '@elastic/eui'
import { useState } from 'react'
import { FormattedMessage } from 'react-intl'

import AdvancedBibliographicSearchControls from './advanced/AdvancedBibliographicSearchControls'
import SimpleBibliographicSearchControls from './simple/SimpleBibliographicSearchControls'
import MaterialTypeSelect from './MaterialTypeSelect'

import type { FC } from 'react'

import type {
  BibliographicMaterial,
  SearchMode,
  SearchQueryTerms,
} from '../api-helpers/search/types'

type Props = {
  onQuerySubmited: (
    materialType: BibliographicMaterial,
    terms: SearchQueryTerms | undefined,
    searchMode: SearchMode,
  ) => void
  isLoading: boolean
}

type UiSearchMode = 'simple' | 'advanced'

const BibliographicSearchControls: FC<Props> = ({
  onQuerySubmited,
  isLoading,
}) => {
  const [uiMode, setUiMode] = useState<UiSearchMode>('simple')

  const [materialType, setMaterialType] = useState<BibliographicMaterial>('all')

  const submit = (terms?: SearchQueryTerms) => {
    let searchMode: SearchMode = 'simple'

    if (terms === undefined) {
      searchMode = 'list_all'
    } else if (uiMode === 'advanced') {
      searchMode = 'advanced'
    }

    onQuerySubmited(materialType, terms, searchMode)
  }

  return (
    <EuiFlexGroup direction='column'>
      <EuiFlexGroup alignItems='center' justifyContent='spaceBetween'>
        <EuiTitle size='s'>
          <h2>
            {uiMode === 'advanced' ? (
              <FormattedMessage
                defaultMessage='Pesquisa Bibliográfica Avançada'
                id='search.bibliographic.header.2.advanced'
              />
            ) : (
              <FormattedMessage
                defaultMessage='Pesquisa Bibliográfica'
                id='search.bibliographic.header.2.simple'
              />
            )}
          </h2>
        </EuiTitle>
        <EuiFlexItem>
          <EuiFlexGroup justifyContent='flexEnd'>
            <EuiFlexItem grow={false}>
              <MaterialTypeSelect
                value={materialType}
                onChange={setMaterialType}
              />
            </EuiFlexItem>
            <EuiFlexItem grow={false}>
              <EuiFlexGroup direction='column' gutterSize='none'>
                <EuiSpacer size='l' />
                <EuiSwitch
                  checked={uiMode === 'advanced'}
                  label={
                    <FormattedMessage
                      defaultMessage='Pesquisa avançada'
                      id='search.bibliographic.advanced_search'
                    />
                  }
                  onChange={() =>
                    setUiMode((current) =>
                      current === 'advanced' ? 'simple' : 'advanced',
                    )
                  }
                />
              </EuiFlexGroup>
            </EuiFlexItem>
          </EuiFlexGroup>
        </EuiFlexItem>
      </EuiFlexGroup>
      <EuiHorizontalRule />
      {uiMode === 'advanced' ? (
        <AdvancedBibliographicSearchControls
          isLoading={isLoading}
          onQuerySubmited={submit}
        />
      ) : (
        <SimpleBibliographicSearchControls
          isLoading={isLoading}
          onQuerySubmited={submit}
        />
      )}
    </EuiFlexGroup>
  )
}

export default BibliographicSearchControls
