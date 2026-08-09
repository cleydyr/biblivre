import { FormattedMessage } from 'react-intl'

import PageTemplate from '../../components/PageTemplate'

import CirculationReturnView from './CirculationReturnView'
import useCirculationReturn from './useCirculationReturn'

const CirculationReturnPage = () => {
  const returnState = useCirculationReturn()

  return (
    <PageTemplate
      pageTitle={
        <FormattedMessage
          defaultMessage='Devolução'
          id='circulation.return.header'
        />
      }
    >
      <CirculationReturnView {...returnState} />
    </PageTemplate>
  )
}

export default CirculationReturnPage
