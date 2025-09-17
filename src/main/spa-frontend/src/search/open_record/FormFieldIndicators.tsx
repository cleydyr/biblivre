import { EuiDescriptionList } from '@elastic/eui'

import { INDICATORS } from '../../api-helpers/search/constants'
import { isDigit } from '../../api-helpers/search/lib'
import {
  getLegacyIndicatorTranslation,
  getLegacyIndicatorValueTranslation,
} from '../../legacy_translations/lib'

import type { FC } from 'react'

import type {
  Digit,
  MarcDatafield,
  MarcDatafieldValue,
} from '../../api-helpers/search/response-types'

type Props = {
  datafield: MarcDatafield
  datafieldValue: MarcDatafieldValue
}

export const FormFieldIndicators: FC<Props> = ({
  datafield,
  datafieldValue,
}) => {
  const listItems = INDICATORS.filter(hasIndicator(datafieldValue)).map(
    (indicator) => {
      return {
        title: getLegacyIndicatorTranslation(datafield, indicator),
        description: getLegacyIndicatorValueTranslation(
          datafield,
          indicator,
          getIndicatorValue(datafieldValue, indicator),
        ),
      }
    },
  )

  return <EuiDescriptionList compressed listItems={listItems} type='column' />
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
