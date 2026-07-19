import { EuiDescriptionList } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import { when } from '../../lib/arrays'
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
    ...when(biblio).elements(biblioItems),
    ...when(holding.accession_number).element((accessionNumber) => ({
      title: (
        <FormattedMessage
          defaultMessage='Tombo patrimonial'
          id='search.holding.accession_number'
        />
      ),
      description: accessionNumber,
    })),
    ...when(user?.name).element((userName) => ({
      title: (
        <FormattedMessage
          defaultMessage='Usuário'
          id='circulation.return.user'
        />
      ),
      description: userName,
    })),
    ...when(holdingLendingBag.lending).element((lending) => ({
      title: (
        <FormattedMessage
          defaultMessage='Data do empréstimo'
          id='circulation.lending.lending_date'
        />
      ),
      description: formatCirculationDateTime(lending.created),
    })),
    ...when(holdingLendingBag.lending?.expectedReturnDate).element(
      (expectedReturnDate) => ({
        title: (
          <FormattedMessage
            defaultMessage='Data prevista para devolução'
            id='circulation.lending.expected_return_date'
          />
        ),
        description: formatCirculationDate(expectedReturnDate),
      }),
    ),
  ]

  return <EuiDescriptionList listItems={items} type='column' />
}

export default HoldingLendingSummary
