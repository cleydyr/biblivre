import {
  EuiButton,
  EuiFieldSearch,
  EuiFlexGroup,
  EuiFlexItem,
  EuiFormRow,
} from '@elastic/eui'
import { useRef, useState } from 'react'
import { FormattedMessage, useIntl } from 'react-intl'

import type { FC } from 'react'

import type { SimpleQueryTerm } from '../../api-helpers/search/types'

type Props = {
  onQuerySubmited: (terms?: SimpleQueryTerm) => void
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
    <EuiFlexGroup alignItems='center' justifyContent='flexStart'>
      <EuiFlexItem grow={false}>
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
            size={32}
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
      </EuiFlexItem>
      <EuiFlexItem grow={false}>
        <EuiButton
          isLoading={isLoading}
          onClick={() => {
            onQuerySubmited(getSearchQuery(query.current))
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
      </EuiFlexItem>
    </EuiFlexGroup>
  )
}

function getSearchQuery(query: string): SimpleQueryTerm | undefined {
  if (query === '') {
    return undefined
  }

  return {
    query,
  }
}

export default SimpleBibliographicSearchControls
