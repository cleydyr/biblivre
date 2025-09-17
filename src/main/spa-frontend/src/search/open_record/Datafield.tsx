import { EuiDescriptionList } from '@elastic/eui'

import { getIndicatorListItems, getSubfieldListItems } from './lib'

import type { FC } from 'react'

import type {
  MarcDatafield,
  MarcDatafieldValue,
} from '../../api-helpers/search/response-types'

type Props = {
  datafield: MarcDatafield
  datafieldValue: MarcDatafieldValue
}

const Datafield: FC<Props> = ({ datafield, datafieldValue }) => {
  const listItems = [
    ...getIndicatorListItems(datafieldValue, datafield),
    ...getSubfieldListItems(datafieldValue, datafield),
  ]

  return (
    <EuiDescriptionList compressed columnGutterSize='m' listItems={listItems} />
  )
}

export default Datafield
