import { EuiFormRow } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import TypedEuiSelect from '../components/TypedEuiSelect'

import type { FC } from 'react'

import type { BibliographicMaterial } from '../api-helpers/search/types'
import type { TypedEuiSelectOption } from '../components/TypedEuiSelect'

type Props = {
  value: BibliographicMaterial
  onChange: (materialType: BibliographicMaterial) => void
}

const MaterialTypeSelect: FC<Props> = ({ value, onChange }) => {
  return (
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
        value={value}
        onChange={(e) => onChange(e.target.value)}
      />
    </EuiFormRow>
  )
}

export default MaterialTypeSelect

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
