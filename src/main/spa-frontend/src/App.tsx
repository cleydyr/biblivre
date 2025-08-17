import { EuiButton, EuiText } from '@elastic/eui'
import { Fragment } from 'react'

import { useSearchCatalographic } from './search/hooks'

const App = () => {
  const {
    mutate: search,
    isPending,
    isSuccess,
    data: searchResults,
  } = useSearchCatalographic()

  return (
    <Fragment>
      <EuiButton
        isLoading={isPending}
        onClick={() => {
          search()
        }}
      >
        Search
      </EuiButton>
      {isSuccess && searchResults.success && (
        <EuiText>
          {searchResults.search.data.map(record => (
            <div key={record.id}>
              <h1>{record.title}</h1>
              <p>{record.author}</p>
            </div>
          ))}
        </EuiText>
      )}
    </Fragment>
  )
}

export default App
