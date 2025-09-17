import { EuiFlexGroup, EuiTextColor } from '@elastic/eui'
import { useIntl } from 'react-intl'

import { INDICATORS } from '../../api-helpers/search/constants'
import { isDigit } from '../../api-helpers/search/lib'
import {
  getLegacyIndicatorTranslation,
  getLegacyIndicatorValueTranslation,
  getLegacySubfieldTranslation,
} from '../../legacy_translations/lib'

import type { Optional } from 'utility-types'

import type {
  Digit,
  MarcDatafield,
  MarcDatafieldValue,
  MarcSubfield,
  OpenResult,
} from '../../api-helpers/search/response-types'

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

export function getIndicatorListItems(
  datafieldValue: MarcDatafieldValue,
  datafield: MarcDatafield,
) {
  return INDICATORS.filter(hasIndicator(datafieldValue)).map((indicator) => {
    return {
      title: (
        <EuiFlexGroup gutterSize='xs'>
          {getLegacyIndicatorTranslation(datafield, indicator)}{' '}
          <EuiTextColor color='subdued'>(#{indicator})</EuiTextColor>
        </EuiFlexGroup>
      ),
      description: getLegacyIndicatorValueTranslation(
        datafield,
        indicator,
        getIndicatorValue(datafieldValue, indicator),
      ),
    }
  })
}

function hasIndicator(
  datafieldValue: MarcDatafieldValue,
): (value: 1 | 2) => boolean {
  return (indicator) => isDigit(datafieldValue[`ind${indicator}`])
}

function getIndicatorValue(
  datafieldValue: MarcDatafieldValue,
  indicator: 1 | 2,
): Digit {
  const indicatorValue = datafieldValue[`ind${indicator}`]

  if (isDigit(indicatorValue)) {
    return indicatorValue as Digit
  }

  throw new Error('Invalid indicator value')
}

export function getSubfieldListItems(
  datafieldValue: MarcDatafieldValue,
  datafield: MarcDatafield,
) {
  return Object.entries(datafieldValue)
    .filter(([key]) => key !== 'ind1' && key !== 'ind2')
    .map(([key, value]) => ({
      title: (
        <EuiFlexGroup gutterSize='xs'>
          {getLegacySubfieldTranslation(datafield, key as MarcSubfield)}{' '}
          <EuiTextColor color='subdued'>(${key})</EuiTextColor>
        </EuiFlexGroup>
      ),
      description: value,
    }))
}
