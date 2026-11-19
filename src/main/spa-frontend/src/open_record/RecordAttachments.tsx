import {
  EuiBasicTable,
  EuiEmptyPrompt,
  EuiFlexGroup,
  EuiIcon,
  EuiLink,
  EuiSpacer,
  EuiText,
} from '@elastic/eui'
import { FormattedMessage, useIntl } from 'react-intl'

import { BIBLIVRE_ENDPOINT } from '../api-helpers/constants'

import type { FC } from 'react'

import type {
  BibliographicRecordAttachment,
  OpenResult,
} from '../api-helpers/search/response-types'

type Props = {
  record: OpenResult
}

const RecordAttachments: FC<Props> = ({ record }) => {
  const { formatMessage } = useIntl()
  const attachments = record.attachments

  if (attachments.length === 0) {
    return (
      <EuiEmptyPrompt
        body={
          <p>
            <FormattedMessage
              defaultMessage='Este registro não possui arquivos digitais anexados.'
              id='bibliographic-record.attachments.empty.body'
            />
          </p>
        }
        icon={<EuiIcon aria-hidden size='xl' type='document' />}
        title={
          <h3>
            <FormattedMessage
              defaultMessage='Nenhum arquivo digital encontrado'
              id='bibliographic-record.attachments.empty.title'
            />
          </h3>
        }
      />
    )
  }

  const columns = [
    {
      field: 'name',
      name: (
        <FormattedMessage
          defaultMessage='Nome do arquivo'
          id='bibliographic-record.attachments.table.name'
        />
      ),
      render: (name: string, attachment: BibliographicRecordAttachment) => (
        <EuiLink external href={getAttachmentUrl(attachment)} target='_blank'>
          {name}
        </EuiLink>
      ),
    },
  ]

  return (
    <EuiFlexGroup direction='column'>
      <div />
      <EuiText>
        <h4>
          <FormattedMessage
            defaultMessage='Arquivos digitais ({count})'
            id='bibliographic-record.attachments.title'
            values={{ count: attachments.length }}
          />
        </h4>
      </EuiText>
      <EuiSpacer size='m' />
      <EuiBasicTable
        columns={columns}
        items={attachments}
        rowHeader='name'
        tableCaption={formatMessage({
          defaultMessage: 'Arquivos digitais',
          id: 'bibliographic-record.attachments.table.caption',
        })}
      />
    </EuiFlexGroup>
  )
}

function getAttachmentUrl(attachment: BibliographicRecordAttachment): string {
  return `${BIBLIVRE_ENDPOINT}/${attachment.uri}`
}

export default RecordAttachments
