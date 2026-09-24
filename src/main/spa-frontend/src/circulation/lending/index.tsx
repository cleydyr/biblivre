import { FormattedMessage } from 'react-intl'

import PageTemplate from '../../components/PageTemplate'

import CirculationLendingView from './CirculationLendingView'
import useCirculationLending from './useCirculationLending'

const CirculationLendingPage = () => {
  const lendingState = useCirculationLending()

  return (
    <PageTemplate
      pageTitle={
        <FormattedMessage
          defaultMessage='Empréstimo e renovação'
          id='circulation.lending.header'
        />
      }
    >
      <CirculationLendingView {...lendingState} />
    </PageTemplate>
  )
}

export default CirculationLendingPage
