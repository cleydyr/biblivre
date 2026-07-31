import {
  EuiFlexGroup,
  EuiFlexItem,
  EuiHorizontalRule,
  EuiTitle,
} from '@elastic/eui'
import { useState } from 'react'
import { FormattedMessage } from 'react-intl'

import MaterialTypeSelect from '../MaterialTypeSelect'
import SimpleBibliographicSearchControls from '../simple/SimpleBibliographicSearchControls'

import type { FC } from 'react'

import type {
  BibliographicMaterial,
  SearchQueryTerms,
} from '../../api-helpers/search/types'

type Props = {
  onQuerySubmited: (
    materialType: BibliographicMaterial,
    terms: SearchQueryTerms,
  ) => void
  isLoading: boolean
}

const IntelligentBibliographicSearchControls: FC<Props> = ({
  onQuerySubmited,
  isLoading,
}) => {
  const [materialType, setMaterialType] = useState<BibliographicMaterial>('all')

  return (
    <EuiFlexGroup direction='column'>
      <EuiFlexGroup alignItems='center' justifyContent='spaceBetween'>
        <EuiTitle size='s'>
          <h2>
            <FormattedMessage
              defaultMessage='Pesquisa Bibliográfica Inteligente'
              id='search.bibliographic.header.2.intelligent'
            />
          </h2>
        </EuiTitle>
        <EuiFlexItem grow={false}>
          <MaterialTypeSelect value={materialType} onChange={setMaterialType} />
        </EuiFlexItem>
      </EuiFlexGroup>
      <EuiHorizontalRule />
      <SimpleBibliographicSearchControls
        allowListAll={false}
        isLoading={isLoading}
        onQuerySubmited={(terms) => {
          if (terms === undefined) {
            return
          }

          onQuerySubmited(materialType, terms)
        }}
      />
    </EuiFlexGroup>
  )
}

export default IntelligentBibliographicSearchControls
