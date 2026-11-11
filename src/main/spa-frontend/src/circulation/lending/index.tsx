import { FormattedMessage } from 'react-intl'

import PageTemplate from '../../components/PageTemplate'

import CirculationLendingView from './CirculationLendingView'
import useCirculationLending from './useCirculationLending'

/** Lending desk (legacy `circulation/lending.jsp`). */
const CirculationLendingPage = () => {
  const lendingState = useCirculationLending()

  return (
    <PageTemplate
      pageTitle={
        <FormattedMessage
          defaultMessage='Empréstimo'
          id='circulation.lending.page.header'
        />
      }
    >
      <CirculationLendingView {...lendingState} />
    </PageTemplate>
  )
}

export default CirculationLendingPage
