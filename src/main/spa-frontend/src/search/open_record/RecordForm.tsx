import { EuiSpacer, EuiText } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import type { FC } from 'react'

import type { OpenResult } from '../../api-helpers/search/response-types'

type Props = {
  record: OpenResult
}

const BibliographicRecordFormPanel: FC<Props> = ({ record }) => {
  return (
    <div>
      <EuiText color='subdued' size='s'>
        <FormattedMessage
          defaultMessage='Visualização do formulário MARC para o registro bibliográfico.'
          id='bibliographic-record.form-panel.description'
        />
      </EuiText>
      <EuiSpacer size='m' />
      <EuiText>
        <p>
          <FormattedMessage
            defaultMessage='Componente em desenvolvimento - ID do registro: {recordId}'
            id='bibliographic-record.form-panel.placeholder'
            values={{ recordId: record.id }}
          />
        </p>
      </EuiText>
    </div>
  )
}

export default BibliographicRecordFormPanel
