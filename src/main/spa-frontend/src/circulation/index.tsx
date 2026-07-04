import { EuiFlexGroup, EuiFlexItem } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import PageTemplate from '../components/PageTemplate'

import CirculationInstructionsAccordion from './CirculationInstructionsAccordion'
import CirculationUsersControlsAndTable from './CirculationUsersControlsAndTable'

const CirculationPage = () => {
  return (
    <PageTemplate
      pageTitle={
        <FormattedMessage
          defaultMessage='Circulação'
          id='circulation.header.1'
        />
      }
    >
      <EuiFlexGroup>
        <EuiFlexItem>
          <CirculationInstructionsAccordion />
          <CirculationUsersControlsAndTable />
        </EuiFlexItem>
      </EuiFlexGroup>
    </PageTemplate>
  )
}

export default CirculationPage
