import { EuiPageTemplate, useEuiTheme } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'
import { useParams } from 'react-router-dom'

import BibliographicRecordDetails from './BibliographicRecordDetails'
import { isValidRecordId } from './lib'

const BibliographicRecordPage = () => {
  const { recordId } = useParams()

  const { euiTheme } = useEuiTheme()

  // TODO: redirect the user to a 404 page to be added later
  if (!isValidRecordId(recordId)) {
    return <div>Record ID not found</div>
  }

  return (
    <EuiPageTemplate
      grow={false}
      paddingSize='xl'
      restrictWidth={euiTheme.breakpoint.l}
    >
      <EuiPageTemplate.Header
        pageTitle={
          <FormattedMessage
            defaultMessage='Registro Bibliográfico'
            id='bibliographic-record.header.1'
          />
        }
      />

      <EuiPageTemplate.Section>
        <BibliographicRecordDetails recordId={Number(recordId)} />
      </EuiPageTemplate.Section>
    </EuiPageTemplate>
  )
}

export default BibliographicRecordPage
