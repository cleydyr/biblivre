import { EuiDescriptionList } from '@elastic/eui'
import { FormattedMessage } from 'react-intl'

import type { FC } from 'react'

import type { OpenResult } from '../../api-helpers/search/response-types'

type Props = {
  record: OpenResult
}

const BibliographicRecordFlyoutHeader: FC<Props> = ({ record }) => {
  return (
    <EuiDescriptionList
      compressed
      listItems={[
        {
          title: (
            <FormattedMessage
              defaultMessage='Título'
              id='search.bibliographic.title'
            />
          ),
          description: record.title,
        },
        {
          title: (
            <FormattedMessage
              defaultMessage='Autor'
              id='search.bibliographic.author'
            />
          ),
          description: record.author,
        },
        {
          title: (
            <FormattedMessage
              defaultMessage='Ano de publicação'
              id='search.bibliographic.publication_year'
            />
          ),
          description: record.publication_year,
        },
        {
          title: (
            <FormattedMessage
              defaultMessage='Localização'
              id='search.bibliographic.shelf_location'
            />
          ),
          description: record.shelf_location,
        },
        {
          title: (
            <FormattedMessage
              defaultMessage='ISBN'
              id='search.bibliographic.isbn'
            />
          ),
          description: record.isbn,
        },
        {
          title: (
            <FormattedMessage
              defaultMessage='ISSN'
              id='search.bibliographic.issn'
            />
          ),
          description: record.issn,
        },
        {
          title: (
            <FormattedMessage
              defaultMessage='ISRC'
              id='search.bibliographic.isrc'
            />
          ),
          description: record.isrc,
        },
        {
          title: (
            <FormattedMessage
              defaultMessage='Nº do registro'
              id='search.bibliographic.id'
            />
          ),
          description: record.id,
        },
      ]}
      type='column'
    />
  )
}

export default BibliographicRecordFlyoutHeader
