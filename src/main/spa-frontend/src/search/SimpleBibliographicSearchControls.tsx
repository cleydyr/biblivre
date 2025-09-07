import { EuiButton, EuiFieldSearch, EuiFlexGroup } from '@elastic/eui'
import { debounce } from 'es-toolkit'
import { useRef } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'

import type { SearchQuery } from '../api-helpers/search/types'
import type { FC, RefObject } from 'react'

type Props = {
  onQuerySubmited: (query?: SearchQuery) => void
  isLoading: boolean
}

const SEARCH_FIELD_DEBOUNCE_MS = 500

const SimpleBibliographicSearchControls: FC<Props> = ({
  onQuerySubmited,
  isLoading,
}) => {
  const { formatMessage } = useIntl()

  const query = useRef<string>('')

  return (
    <EuiFlexGroup>
      <EuiButton
        isLoading={isLoading}
        onClick={() => {
          onQuerySubmited(getSearchQuery(query))
        }}
      >
        {query ? (
          <FormattedMessage
            defaultMessage='Pesquisar'
            id='search.bibliographic.search'
          />
        ) : (
          <FormattedMessage
            defaultMessage='Listar todos'
            id='search.bibliographic.search_all'
          />
        )}
      </EuiButton>
      <EuiFieldSearch
        placeholder={formatMessage({
          defaultMessage: 'Preencha os termos da pesquisa',
          id: 'search.bibliographic.search_placeholder',
        })}
        onChange={debounce((e) => {
          const searchTerm = e.target.value

          query.current = searchTerm
        }, SEARCH_FIELD_DEBOUNCE_MS)}
        onSearch={(searchTerm) => {
          onQuerySubmited({
            query: searchTerm,
          })
        }}
      />
    </EuiFlexGroup>
  )
}

function getSearchQuery(query: RefObject<string>): SearchQuery | undefined {
  if (query.current === '') {
    return undefined
  }

  return {
    query: query.current,
  }
}

export default SimpleBibliographicSearchControls
