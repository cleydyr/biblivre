import { EuiDescriptionList } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import { element, elements } from '../../lib/arrays'
import {
  formatCirculationDate,
  formatCirculationDateTime,
  useBiblioDescriptionListItems,
} from '../lib'

import type { FC } from 'react'

import type { HoldingLendingBag } from '../../api-helpers/circulation/response-types'
import type { EuiDescriptionListItem } from '../../components/types'

type Props = {
  holdingLendingBag: HoldingLendingBag
}

const HoldingLendingSummary: FC<Props> = ({ holdingLendingBag }) => {
  const { biblio, holding, user } = holdingLendingBag
  const shelfLocation = [biblio?.shelf_location, holding.location_d]
    .filter(Boolean)
    .join(' ')

  const biblioItems = useBiblioDescriptionListItems(
    biblio ?? {
      title: '',
      author: '',
      publication_year: '',
      shelf_location: '',
    },
    shelfLocation,
  )

  const items: EuiDescriptionListItem[] = [
    ...elements(biblioItems).if(Boolean(biblio)),
    ...element({
      title: (
        <FormattedMessage
          defaultMessage='Tombo patrimonial'
          id='search.holding.accession_number'
        />
      ),
      description: holding.accession_number,
    }).if(Boolean(holding.accession_number)),
    ...element({
      title: (
        <FormattedMessage
          defaultMessage='Usuário'
          id='circulation.return.user'
        />
      ),
      description: user?.name ?? '',
    }).if(Boolean(user?.name)),
    ...element({
      title: (
        <FormattedMessage
          defaultMessage='Data do empréstimo'
          id='circulation.lending.lending_date'
        />
      ),
      description: holdingLendingBag.lending
        ? formatCirculationDateTime(holdingLendingBag.lending.created)
        : '',
    }).if(Boolean(holdingLendingBag.lending)),
    ...element({
      title: (
        <FormattedMessage
          defaultMessage='Data prevista para devolução'
          id='circulation.lending.expected_return_date'
        />
      ),
      description: holdingLendingBag.lending?.expectedReturnDate
        ? formatCirculationDate(holdingLendingBag.lending.expectedReturnDate)
        : '',
    }).if(Boolean(holdingLendingBag.lending?.expectedReturnDate)),
  ]

  return <EuiDescriptionList listItems={items} type='column' />
}

export default HoldingLendingSummary
