import { EuiFormRow } from '@elastic/eui'
import { useState } from 'react'
import { FormattedMessage } from 'react-intl'

import { FIELDS } from '../api-helpers/search/constants'
import TypedEuiSelect from '../components/TypedEuiSelect'

import type { FC } from 'react'

import type { EncodedQueryField } from '../api-helpers/search/types'
import type { TypedEuiSelectOption } from '../components/TypedEuiSelect'

type Props = {
  onSortChange: (sort: EncodedQueryField) => void
}

const BibliographicSearchResultSort: FC<Props> = ({ onSortChange }) => {
  const options: TypedEuiSelectOption<EncodedQueryField>[] =
    useBibliographicSearchSortOptions()

  const [sort, setSort] = useState<EncodedQueryField>(FIELDS.TITLE)

  return (
    <EuiFormRow
      label={
        <FormattedMessage
          defaultMessage='Ordenar por'
          id='bibliographic-search-result-sort.label'
        />
      }
    >
      <TypedEuiSelect<EncodedQueryField>
        compressed
        options={options}
        value={sort}
        onChange={(e) => {
          const changedValue = e.target.value

          setSort(changedValue)

          onSortChange(changedValue)
        }}
      />
    </EuiFormRow>
  )
}

const useBibliographicSearchSortOptions =
  (): TypedEuiSelectOption<EncodedQueryField>[] => {
    return [
      {
        value: FIELDS.AUTHOR,
        text: (
          <FormattedMessage
            defaultMessage='Autor'
            id='bibliographic-search-result-sort.author'
          />
        ),
      },
      {
        value: FIELDS.YEAR,
        text: (
          <FormattedMessage
            defaultMessage='Ano'
            id='bibliographic-search-result-sort.ano'
          />
        ),
      },
      {
        value: FIELDS.TITLE,
        text: (
          <FormattedMessage
            defaultMessage='Título'
            id='bibliographic-search-result-sort.title'
          />
        ),
      },
      {
        value: FIELDS.PUBLISHER,
        text: (
          <FormattedMessage
            defaultMessage='Editora'
            id='bibliographic-search-result-sort.publisher'
          />
        ),
      },
      {
        value: FIELDS.SERIES,
        text: (
          <FormattedMessage
            defaultMessage='Série'
            id='bibliographic-search-result-sort.series'
          />
        ),
      },
    ]
  }

export default BibliographicSearchResultSort
