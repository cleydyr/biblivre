import { useIntl } from 'react-intl'

import type { Optional } from 'utility-types'

import type { OpenResult } from '../../api-helpers/search/response-types'

type EuiDescriptionListItem = {
  title: string
  description: string
}

export function usePanelDescriptionListItems(
  record: OpenResult,
): EuiDescriptionListItem[] {
  const { formatMessage } = useIntl()

  return [
    {
      title: formatMessage({
        defaultMessage: 'Título',
        id: 'search.bibliographic.title',
      }),
      description: record.title,
    },
    {
      title: formatMessage({
        defaultMessage: 'Autor',
        id: 'search.bibliographic.author',
      }),
      description: record.author,
    },
    {
      title: formatMessage({
        defaultMessage: 'Ano de publicação',
        id: 'search.bibliographic.publication_year',
      }),
      description: record.publication_year,
    },
    {
      title: formatMessage({
        defaultMessage: 'Localização',
        id: 'search.bibliographic.shelf_location',
      }),
      description: record.shelf_location,
    },
    {
      title: formatMessage({
        defaultMessage: 'ISBN',
        id: 'search.bibliographic.isbn',
      }),
      description: record.isbn,
    },
    {
      title: formatMessage({
        defaultMessage: 'ISSN',
        id: 'search.bibliographic.issn',
      }),
      description: record.issn,
    },
    {
      title: formatMessage({
        defaultMessage: 'ISRC',
        id: 'search.bibliographic.isrc',
      }),
      description: record.isrc,
    },
    {
      title: formatMessage({
        defaultMessage: 'Nº do registro',
        id: 'search.bibliographic.id',
      }),
      description: String(record.id),
    },
  ].filter(isValidEuiDescriptionListItem)
}

function isValidEuiDescriptionListItem(
  item: Optional<EuiDescriptionListItem, 'description'>,
): item is EuiDescriptionListItem {
  return item.description !== undefined
}
