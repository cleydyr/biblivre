import { EuiButton, EuiEmptyPrompt } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import messages from './messages'

/** Shown when a render error escapes every component; it is already reported. */
const AppErrorFallback = () => (
  <EuiEmptyPrompt
    actions={
      <EuiButton fill onClick={() => window.location.reload()}>
        <FormattedMessage {...messages.reload} />
      </EuiButton>
    }
    body={
      <p>
        <FormattedMessage {...messages.body} />
      </p>
    }
    color='danger'
    iconType='error'
    title={
      <h2>
        <FormattedMessage {...messages.title} />
      </h2>
    }
  />
)

export default AppErrorFallback
