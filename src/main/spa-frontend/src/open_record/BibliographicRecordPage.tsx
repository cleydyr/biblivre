import { FormattedMessage } from 'react-intl'
import { useParams } from 'react-router-dom'

import PageTemplate from '../components/PageTemplate'

import BibliographicRecordDetails from './BibliographicRecordDetails'
import { isValidRecordId } from './lib'

const BibliographicRecordPage = () => {
  const { recordId } = useParams()

  // TODO: redirect the user to a 404 page to be added later
  if (!isValidRecordId(recordId)) {
    return <div>Record ID not found</div>
  }

  return (
    <PageTemplate
      pageTitle={
        <FormattedMessage
          defaultMessage='Registro Bibliográfico'
          id='bibliographic-record.header.1'
        />
      }
    >
      <BibliographicRecordDetails recordId={Number(recordId)} />
    </PageTemplate>
  )
}

export default BibliographicRecordPage
