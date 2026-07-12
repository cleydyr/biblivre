import { EuiButton, EuiFlexGroup, EuiFlexItem } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import PageTemplate from '../components/PageTemplate'

import CirculationInstructionsAccordion from './CirculationInstructionsAccordion'
import CirculationUsersControlsAndTable from './CirculationUsersControlsAndTable'
import useCirculationUsersControlsAndTable from './useCirculationUsersControlsAndTable'

const CirculationPage = () => {
  const { onCreateUserClick } = useCirculationUsersControlsAndTable()

  return (
    <PageTemplate
      pageTitle={
        <FormattedMessage
          defaultMessage='Circulação'
          id='circulation.header.1'
        />
      }
      rightSideItems={[
        <EuiButton
          key='new-user'
          iconType='plusInCircle'
          onClick={onCreateUserClick}
        >
          <FormattedMessage
            defaultMessage='Novo usuário'
            id='circulation.user.button.new'
          />
        </EuiButton>,
      ]}
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
