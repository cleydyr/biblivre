import {
  EuiFlexGroup,
  EuiFlexItem,
  EuiFormRow,
  EuiHorizontalRule,
  EuiSpacer,
  EuiSwitch,
  EuiTitle,
} from '@elastic/eui'
import { useState } from 'react'
import { FormattedMessage } from 'react-intl'

import TypedEuiSelect from '../components/TypedEuiSelect'
import AdvancedBibliographicSearchControls from './advanced/AdvancedBibliographicSearchControls'
import SimpleBibliographicSearchControls from './simple/SimpleBibliographicSearchControls'

import type { FC } from 'react'

import type {
  BibliographicMaterial,
  SearchMode,
  SearchQueryTerms,
} from '../api-helpers/search/types'
import type { TypedEuiSelectOption } from '../components/TypedEuiSelect'

type Props = {
  onQuerySubmited: (
    materialType: BibliographicMaterial,
    terms: SearchQueryTerms | undefined,
    searchMode: SearchMode,
  ) => void
  isLoading: boolean
}

type UiSearchMode = 'simple' | 'advanced' | 'intelligent'

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
    } else if (uiMode === 'intelligent' || uiMode === 'advanced') {
      searchMode = uiMode
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
            ) : uiMode === 'intelligent' ? (
              <FormattedMessage
                defaultMessage='Pesquisa Bibliográfica Inteligente'
                id='search.bibliographic.header.2.intelligent'
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
              <EuiFormRow
                label={
                  <FormattedMessage
                    defaultMessage='Tipo de material'
                    id='search.bibliographic.material_type'
                  />
                }
              >
                <TypedEuiSelect<BibliographicMaterial>
                  compressed
                  options={getMaterialOptions()}
                  value={materialType}
                  onChange={(e) => setMaterialType(e.target.value)}
                />
              </EuiFormRow>
            </EuiFlexItem>
            <EuiFlexItem grow={false}>
              <EuiFlexGroup direction='column' gutterSize='none'>
                <EuiSpacer size='l' />
                <EuiSwitch
                  checked={uiMode === 'intelligent'}
                  label={
                    <FormattedMessage
                      defaultMessage='Pesquisa inteligente'
                      id='search.bibliographic.intelligent_search'
                    />
                  }
                  onChange={() =>
                    setUiMode((current) =>
                      current === 'intelligent' ? 'simple' : 'intelligent',
                    )
                  }
                />
              </EuiFlexGroup>
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

function getMaterialOptions(): TypedEuiSelectOption<BibliographicMaterial>[] {
  return [
    {
      value: 'all',
      text: <FormattedMessage defaultMessage='Todos' id='material-type.all' />,
    },
    {
      value: 'book',
      text: <FormattedMessage defaultMessage='Livro' id='material-type.book' />,
    },
    {
      value: 'pamphlet',
      text: (
        <FormattedMessage
          defaultMessage='Panfleto'
          id='material-type.pamphlet'
        />
      ),
    },
    {
      value: 'manuscript',
      text: (
        <FormattedMessage
          defaultMessage='Manuscrito'
          id='material-type.manuscript'
        />
      ),
    },
    {
      value: 'thesis',
      text: (
        <FormattedMessage defaultMessage='Tese' id='material-type.thesis' />
      ),
    },
    {
      value: 'periodic',
      text: (
        <FormattedMessage
          defaultMessage='Periódico'
          id='material-type.periodic'
        />
      ),
    },
    {
      value: 'articles',
      text: (
        <FormattedMessage defaultMessage='Artigo' id='material-type.articles' />
      ),
    },
    {
      value: 'computer_legible',
      text: (
        <FormattedMessage
          defaultMessage='Arquivo de Computador'
          id='material-type.computer_legible'
        />
      ),
    },
    {
      value: 'map',
      text: <FormattedMessage defaultMessage='Mapa' id='material-type.map' />,
    },
    {
      value: 'photo',
      text: <FormattedMessage defaultMessage='Foto' id='material-type.photo' />,
    },
    {
      value: 'movie',
      text: (
        <FormattedMessage defaultMessage='Filme' id='material-type.movie' />
      ),
    },
    {
      value: 'score',
      text: (
        <FormattedMessage defaultMessage='Partitura' id='material-type.score' />
      ),
    },
    {
      value: 'music',
      text: (
        <FormattedMessage defaultMessage='Música' id='material-type.music' />
      ),
    },
    {
      value: 'nonmusical_sound',
      text: (
        <FormattedMessage
          defaultMessage='Som não musical'
          id='material-type.nonmusical_sound'
        />
      ),
    },
    {
      value: 'object_3d',
      text: (
        <FormattedMessage
          defaultMessage='Objeto 3D'
          id='material-type.object_3d'
        />
      ),
    },
  ]
}
