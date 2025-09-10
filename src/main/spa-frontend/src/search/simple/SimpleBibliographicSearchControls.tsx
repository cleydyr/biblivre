import {
  EuiButton,
  EuiFieldSearch,
  EuiFlexGroup,
  EuiFormRow,
} from '@elastic/eui'
import { useRef, useState } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'

import type { FC, RefObject } from 'react'

import type { SearchQueryTerms } from '../../api-helpers/search/types'

type Props = {
  onQuerySubmited: (terms?: SearchQueryTerms) => void
  isLoading: boolean
}

const SimpleBibliographicSearchControls: FC<Props> = ({
  onQuerySubmited,
  isLoading,
}) => {
  const { formatMessage } = useIntl()

  const query = useRef<string>('')

  const [isListAll, setListAll] = useState<boolean>(true)

  return (
    <EuiFlexGroup>
      <EuiButton
        isLoading={isLoading}
        onClick={() => {
          onQuerySubmited(getSearchQuery(query))
        }}
      >
        {isListAll ? (
          <FormattedMessage
            defaultMessage='Listar todos'
            id='search.bibliographic.search_all'
          />
        ) : (
          <FormattedMessage
            defaultMessage='Pesquisar'
            id='search.bibliographic.search'
          />
        )}
      </EuiButton>
      <EuiFormRow>
        <EuiFieldSearch
          aria-label={formatMessage({
            defaultMessage: 'Termos da pesquisa simples',
            id: 'search.bibliographic.search_label',
          })}
          placeholder={formatMessage({
            defaultMessage: 'Preencha os termos da pesquisa',
            id: 'search.bibliographic.search_placeholder',
          })}
          onChange={(e) => {
            const searchTerm = e.target.value

            query.current = searchTerm

            setListAll(searchTerm === '')
          }}
          onSearch={(searchTerm) => {
            if (searchTerm === '') {
              // We don't want the clean button to trigger a search submission
              setListAll(true)

              return
            }

            onQuerySubmited({
              query: searchTerm,
            })
          }}
        />
      </EuiFormRow>
    </EuiFlexGroup>
  )
}

function getSearchQuery(
  query: RefObject<string>
): SearchQueryTerms | undefined {
  if (query.current === '') {
    return undefined
  }

  return {
    query: query.current,
  }
}

export default SimpleBibliographicSearchControls
