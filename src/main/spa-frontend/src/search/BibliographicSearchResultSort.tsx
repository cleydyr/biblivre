import { EuiSelect, type EuiSelectOption } from '@elastic/eui'
import { type FC, useState } from 'react'
import { FormattedMessage } from 'react-intl'

import { FIELDS } from '../api-helpers/search/constants'

type Props = {
  onSortChange: (sort: number) => void
}

const BibliographicSearchResultSort: FC<Props> = ({ onSortChange }) => {
  const options: EuiSelectOption[] = useBibliographicSearchSortOptions()

  const [sort, setSort] = useState<string>(FIELDS.TITLE)

  return (
    <EuiSelect
      options={options}
      value={sort}
      onChange={(e) => {
        const changedValue = e.target.value

        setSort(changedValue)

        onSortChange(parseInt(changedValue))
      }}
    />
  )
}

const useBibliographicSearchSortOptions = (): EuiSelectOption[] => {
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
