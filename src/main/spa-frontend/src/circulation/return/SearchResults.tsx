import { EuiEmptyPrompt, EuiFlexGroup } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import HoldingResultCard from './HoldingResultCard'

import type { FC } from 'react'

import type { HoldingLendingBag } from '../../api-helpers/circulation/response-types'

type Props = {
  results: HoldingLendingBag[]
  isBusy: boolean
  onReturn: (lendingId: number) => void
}

const SearchResults: FC<Props> = ({ results, isBusy, onReturn }) => {
  if (results.length === 0) {
    return (
      <EuiEmptyPrompt
        body={
          <FormattedMessage
            defaultMessage='Nenhum exemplar encontrado'
            id='circulation.return.search.empty'
          />
        }
        iconType='search'
      />
    )
  }

  return (
    <EuiFlexGroup direction='column' gutterSize='m'>
      {results.map((bag) => (
        <HoldingResultCard
          key={bag.holding.id}
          holdingLendingBag={bag}
          isBusy={isBusy}
          onReturn={onReturn}
        />
      ))}
    </EuiFlexGroup>
  )
}

export default SearchResults
